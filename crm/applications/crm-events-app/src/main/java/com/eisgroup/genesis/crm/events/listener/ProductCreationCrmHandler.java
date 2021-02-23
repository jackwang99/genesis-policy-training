/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.events.listener;

import com.eisgroup.genesis.commands.services.CrmValidationErrorDefinition;
import com.eisgroup.genesis.commands.validator.errors.ValidationErrorException;
import com.eisgroup.genesis.crm.commands.CustomerState;
import com.eisgroup.genesis.events.EventEnvelope;
import com.eisgroup.genesis.events.handler.StreamEventHandler;
import com.eisgroup.genesis.exception.ErrorHolder;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.modeling.types.immutable.Customer;
import com.eisgroup.genesis.factory.modeling.types.immutable.PolicySummary;
import com.eisgroup.genesis.factory.utils.JsonPropertyAccess;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.json.link.EntityLink;
import com.eisgroup.genesis.json.link.EntityLinkBuilderRegistry;
import com.eisgroup.genesis.json.link.EntityResolver;
import com.eisgroup.genesis.json.link.LinkingParams;
import com.eisgroup.genesis.lifecycle.events.CommandExecutedEvent;
import com.eisgroup.genesis.model.Variation;
import com.eisgroup.genesis.model.external.ExternalModelRepository;
import com.eisgroup.genesis.model.external.error.ExternalModelNotAvailableException;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.policy.core.lifecycle.event.PolicyIntegrationEnvelope;
import com.eisgroup.genesis.policy.core.lifecycle.event.PolicyIntegrationMessage;
import com.eisgroup.genesis.policy.core.model.PolicyVariations;
import com.eisgroup.genesis.policy.lifecycle.commands.event.CustomerValidationFailureEvent;
import com.eisgroup.genesis.policy.lifecycle.commands.event.CustomerValidationSuccessEvent;
import com.eisgroup.genesis.repository.ReadContext;
import com.eisgroup.genesis.streams.CommandExecutedMessageMetadata;
import com.eisgroup.genesis.streams.MessageMetadata;
import com.eisgroup.genesis.streams.publisher.MessagePublisher;
import com.google.common.collect.Sets;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.eisgroup.genesis.streams.consumer.MessageRetryContext;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static java.util.Objects.nonNull;

/**
 * Listens to command executed events. These events are then filtered by
 * {@link #filterFunction} that is passed through constructor. Actual event does
 * not matter as assumption is made that all events contains URI for policy.
 * Policy is then loaded({@link #createReadContext(String)}) and transformed to
 * command({@link #createBillingProductRequest(PolicySummary)}).
 *
 * @author astasauskas
 *
 */
public class ProductCreationCrmHandler extends BaseExternalEventHandler {

    private static final Set<String> ALLOWED_STATES = Sets.newHashSet(CustomerState.qualified.name(), CustomerState.customer.name());

    private static final Logger logger = LoggerFactory.getLogger(ProductCreationCrmHandler.class);
    private final MessagePublisher messagePublisher;

    private final EntityLinkBuilderRegistry entityLinkBuilderRegistry;

    private final EntityResolver entityResolver;

    public ProductCreationCrmHandler(Predicate<String> filterFunction,
                                     EntityLinkBuilderRegistry entityLinkBuilderRegistry, MessagePublisher messagePublisher, EntityResolver entityResolver,
                                     ExternalModelRepository externalModelRepo) {
        super(filterFunction, externalModelRepo);
        this.entityLinkBuilderRegistry = entityLinkBuilderRegistry;
        this.messagePublisher = messagePublisher;
        this.entityResolver = entityResolver;
    }

    @Override
    public boolean supports(MessageMetadata message) {
        return Optional.ofNullable(message)
            .filter(messageMetadata -> messageMetadata instanceof CommandExecutedMessageMetadata)
            .map(CommandExecutedMessageMetadata.class::cast)
            .map(metadata -> {
                logDomainModels(metadata);
                return metadata;
            })
            .map(CommandExecutedMessageMetadata::getCommandName)
            .map(commandName -> filterFunction.test(commandName))
            .orElse(false);
    }
    
    @Override
    public Completable handle(CommandExecutedEvent message) {
        return Optional.ofNullable(message.getOutput())
                .map(entity -> {
                    if (!(entity instanceof PolicySummary)) {
                        String modelName = JsonPropertyAccess.getString(entity.toJson(), "_modelName");
                        logger.warn("#18158: entity with modelName " + modelName + "cannot be casted to PolicySummary");
                        checkDomainModel(modelName);
                    }

                    return entity;
                })
                .filter(entity -> entity instanceof PolicySummary)
                .map(entity -> doHandle((PolicySummary)entity, message))
                .orElse(Completable.complete());
    }

    private Completable doHandle(PolicySummary policy, CommandExecutedEvent message) {
        String policyUri = entityLinkBuilderRegistry.getByType(policy.getClass())
                .createLink(policy, LinkingParams.just(Variation.class, PolicyVariations.QUOTE)
                        .with(DomainModel.class, modelRepo.getActiveModel(policy.getModelName())))
                .getURIString();
        return Single.defer(() -> resolveCustomerAccount(policy, policyUri))
                .flatMapCompletable(customerKey ->
                    publishMessage(new CustomerValidationSuccessEvent(policyUri))
                )
                .onErrorResumeNext(error -> {
                    logger.error("Customer validation failed for policy {}, ", policyUri, error);
                    return publishMessage(createFailureEvent(error, policyUri));
                });
    }

    private Completable publishMessage(PolicyIntegrationMessage message) {
        return messagePublisher.publishAsync(new PolicyIntegrationEnvelope(message));
    }

    private Single<RootEntityKey> resolveCustomerAccount(PolicySummary entity, String policyUri) {
        return Single.just(entity)
            .filter(policy -> nonNull(policy.getCustomer()))
            .switchIfEmpty(commonError("Customer is not set on policy"))
            .map(PolicySummary::getCustomer)
            .filter(customerLink -> nonNull(customerLink.getURI()))
            .switchIfEmpty(commonError("Cannot parse Customer URI"))
            .map(EntityLink::getURI)
            .flatMapSingle(cURI -> entityResolver.resolve(cURI, Customer.class, new ReadContext.Builder().build()))
            .filter(customer -> ALLOWED_STATES.contains(customer.getState()))
            .switchIfEmpty(Maybe.error(() -> new ValidationErrorException(CrmValidationErrorDefinition.POLICY_FOR_LEAD_ERROR.builder().build())))
            .map(Customer::getKey)
            .toSingle();
    }

    private <T> Maybe<T> commonError(String reason) {
        ErrorHolder errorHolder = CrmValidationErrorDefinition.POLICY_INTEGRATION_FAILED.builder()
            .params(reason)
            .build();

        return Maybe.error(() -> new ValidationErrorException(errorHolder));
    }

    private CustomerValidationFailureEvent createFailureEvent(Throwable error, String policyUri) {
        if (error instanceof ValidationErrorException) {
            ErrorHolder errorHolder = ((ValidationErrorException) error).getErrorHolder();
            return new CustomerValidationFailureEvent(policyUri, errorHolder);
        }

        return new CustomerValidationFailureEvent(policyUri);
    }
}