/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.events.listener;

import com.eisgroup.genesis.events.EventEnvelope;
import com.eisgroup.genesis.events.OrganizationValidationFailureEvent;
import com.eisgroup.genesis.events.OrganizationValidationSuccessEvent;
import com.eisgroup.genesis.events.error.OrganizationStructEventErrorException;
import com.eisgroup.genesis.events.error.OrganizationalStructAssigmentsErrorDefintion;
import com.eisgroup.genesis.events.handler.StreamEventHandler;
import com.eisgroup.genesis.exception.ErrorHolder;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.modeling.types.OrganizationalPerson;
import com.eisgroup.genesis.factory.modeling.types.immutable.Organization;
import com.eisgroup.genesis.factory.modeling.types.immutable.OrganizationalAssignment;
import com.eisgroup.genesis.factory.modeling.types.immutable.PolicySummary;
import com.eisgroup.genesis.json.link.EntityLink;
import com.eisgroup.genesis.json.link.EntityLinkBuilderRegistry;
import com.eisgroup.genesis.json.link.EntityLinkResolver;
import com.eisgroup.genesis.json.link.EntityLinkResolverRegistry;
import com.eisgroup.genesis.json.link.LinkingParams;
import com.eisgroup.genesis.lifecycle.events.CommandExecutedEvent;
import com.eisgroup.genesis.model.Variation;
import com.eisgroup.genesis.model.external.ExternalModelRepository;
import com.eisgroup.genesis.model.external.error.ExternalModelNotAvailableException;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.orgstruct.repository.api.OrganizationStructureRepository;
import com.eisgroup.genesis.policy.core.lifecycle.event.PolicyIntegrationEnvelope;
import com.eisgroup.genesis.policy.core.lifecycle.event.PolicyIntegrationMessage;
import com.eisgroup.genesis.policy.core.model.PolicyVariations;
import com.eisgroup.genesis.repository.ReadContext;
import com.eisgroup.genesis.streams.CommandExecutedMessageMetadata;
import com.eisgroup.genesis.streams.MessageMetadata;
import com.eisgroup.genesis.streams.publisher.MessagePublisher;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.eisgroup.genesis.streams.consumer.MessageRetryContext;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * {@link CommandExecutedEvent} based validator, that listens for policy BOR transfer command executed events filtered by {@link #filterFunction},
 * that is passed through constructor, and validates policy Agency (Organization) and Agent (Organizational Person) assignments.
 * 
 * @author adainelis
 *
 */
public class OrganizationAssigmentsEventValidator implements StreamEventHandler<CommandExecutedEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(OrganizationAssigmentsEventValidator.class);
    
    private static final String NAME = "OrganizationAssigmentsEventValidator";
    
    private final Predicate<String> filterFunction;
    private final MessagePublisher messagePublisher;
    private final EntityLinkBuilderRegistry entityLinkBuilderRegistry;
    private final EntityLinkResolverRegistry entityLinkResolverRegistry;
    private final ModelRepository<DomainModel> modelRepo;
    private final OrganizationStructureRepository organizationStructureRepository;
    private final String orgPersonModelName;
    
    
    public OrganizationAssigmentsEventValidator(Predicate<String> filterFunction,
            EntityLinkBuilderRegistry entityLinkBuilderRegistry, EntityLinkResolverRegistry entityLinkResolverRegistry,
            MessagePublisher messagePublisher, ExternalModelRepository externalModelRepo,
            OrganizationStructureRepository organizationStructureRepository, String orgPersonModelName) {
        
        this.filterFunction = filterFunction;
        this.entityLinkBuilderRegistry = entityLinkBuilderRegistry;
        this.entityLinkResolverRegistry = entityLinkResolverRegistry;
        this.messagePublisher = messagePublisher;
        this.modelRepo = externalModelRepo.subscribeAsModelRepository(DomainModel.class);
        this.organizationStructureRepository = organizationStructureRepository;
        this.orgPersonModelName = orgPersonModelName;
    }
    
    @Override
    public boolean supports(MessageMetadata message) {
        return Optional.ofNullable(message)
            .filter(messageMetadata -> messageMetadata instanceof CommandExecutedMessageMetadata)
            .map(CommandExecutedMessageMetadata.class::cast)
            .map(CommandExecutedMessageMetadata::getCommandName)
            .map(filterFunction::test)
            .orElse(false);
    }
    
    @Override
    public Completable handle(CommandExecutedEvent message) {
        return Optional.ofNullable(message.getOutput())
                .filter(entity -> entity instanceof PolicySummary)
                .map(entity -> doHandle((PolicySummary)entity))
                .orElse(Completable.complete());
    }

    @Override
    public Completable handleRetry(MessageRetryContext<EventEnvelope<CommandExecutedEvent>> context) {
        int retryCount = 3;
        Throwable error = context.getError();
        int attempt = context.getAttemptNumber();
        if(error instanceof ExternalModelNotAvailableException && attempt < retryCount) {
            long waitSec = (long) Math.pow(2, attempt); // incremental delay of  2, 4, 8 and 16 seconds
            logger.warn("{} has failed with delayed retry exception, retry {}/{} will occur in in {} sec, error message: {}",
                    getGroupName(), attempt, retryCount, waitSec, error.getMessage());
            return Completable.timer(waitSec, TimeUnit.SECONDS);
        }
        return Completable.error(error);
    }
    
    private Completable doHandle(PolicySummary policy) {
        String policyUri = entityLinkBuilderRegistry.getByType(policy.getClass())
                .createLink(policy, LinkingParams.just(Variation.class, PolicyVariations.POLICY)
                        .with(DomainModel.class, modelRepo.getActiveModel(policy.getModelName())))
                .getURIString();

        return Single.defer(() -> resolveOrganizationAssigment(policy))
                .flatMapCompletable(organization ->
                    publishMessage(new OrganizationValidationSuccessEvent(NAME, policyUri))
                )
                .onErrorResumeNext(error -> {
                    logger.error("Organization assigment validation failed for policy {}, ", policyUri, error);
                    return publishMessage(createFailureEvent(error, policyUri));
                });
    }

    private Completable publishMessage(PolicyIntegrationMessage message) {
        return messagePublisher.publishAsync(new PolicyIntegrationEnvelope(message));
    }

    private Single<Organization> resolveOrganizationAssigment(PolicySummary policy) {
        return Single.just(policy)
            .filter(policySummary -> policySummary.getBusinessDimensions() != null)
            .switchIfEmpty(Maybe.error(() -> buildOrganizationalStructAssigmentsError(
                    OrganizationalStructAssigmentsErrorDefintion.BUSINESS_DIMENSIONS_ATTRIBUTE_IS_MANDATORY)))
            .map(PolicySummary::getBusinessDimensions)
            .flatMapSingle(dimensions -> {
                String agencyCd = dimensions.getAgency();
                String agentCd = dimensions.getSubProducer();
                
                if(StringUtils.isEmpty(agentCd)) {
                    return Single.error(() -> buildOrganizationalStructAssigmentsError(
                            OrganizationalStructAssigmentsErrorDefintion.AGENT_CANNOT_BE_EMPTY));
                }
                
                if(StringUtils.isEmpty(agencyCd)) {
                    return Single.error(() -> buildOrganizationalStructAssigmentsError(
                            OrganizationalStructAssigmentsErrorDefintion.AGENCY_CANNOT_BE_EMPTY));
                }
                
                return organizationStructureRepository.loadOrganizationalPersonByUserName(agentCd, orgPersonModelName)
                        .switchIfEmpty(Observable.error(() -> buildOrganizationalStructAssigmentsError(
                                OrganizationalStructAssigmentsErrorDefintion.AGENT_NOT_ORGANIZATION_PERSON, agentCd)))
                        .flatMap(orgPerson -> {
                            if (isNotInEffectiveInterval(orgPerson.getEffectiveDate(), orgPerson.getExpirationDate())) {
                                return Observable.error(() -> buildOrganizationalStructAssigmentsError(
                                        OrganizationalStructAssigmentsErrorDefintion.AGENT_NOT_EFFECTIVE,
                                        agentCd, orgPerson.getEffectiveDate(), orgPerson.getExpirationDate()));
                            }
                            
                            return Observable.fromIterable(retrieveOrganizationalAssignments(orgPerson));
                        })
                        .flatMapSingle(assignment -> getResolverForLink(assignment.getOrganization())
                                .resolve(assignment.getOrganization(), new ReadContext.Builder().build())
                                .map(organization -> Pair.of(assignment, organization))
                                .onErrorResumeNext(error -> Single.error(
                                        buildOrganizationCannotBeResolvedError(assignment, error)))
                        )
                        .filter(pair -> pair.getRight().getOrganizationCd().equals(agencyCd))
                        .switchIfEmpty(Observable.error(() -> buildOrganizationalStructAssigmentsError(
                                OrganizationalStructAssigmentsErrorDefintion.AGENT_NOT_ASSIGNED_TO_ORGANIZAITON,
                                agentCd, agencyCd)))
                        .filter(pair -> !isNotInEffectiveInterval(
                                pair.getLeft().getEffectiveDate(),
                                pair.getLeft().getExpirationDate()))
                        .switchIfEmpty(Observable.error(() -> buildOrganizationalStructAssigmentsError(
                                OrganizationalStructAssigmentsErrorDefintion.AGENT_HAS_NO_EFFECTIVE_ORGANIZAITON,
                                agentCd, agencyCd)))
                        .singleOrError()
                        .map(Pair::getRight);
            });
            
    }

    private Collection<OrganizationalAssignment> retrieveOrganizationalAssignments(OrganizationalPerson orgPerson) {
        return orgPerson.getOrganizationAssignments();
    }

    private OrganizationStructEventErrorException buildOrganizationCannotBeResolvedError(OrganizationalAssignment assignment,
            Throwable error) {
        return new OrganizationStructEventErrorException(
                OrganizationalStructAssigmentsErrorDefintion.ORGANIZATION_CANNOT_BE_RESOLVED.builder()
                .params(assignment.getOrganization().getURIString()).build(), error);
    }

    private OrganizationStructEventErrorException buildOrganizationalStructAssigmentsError(
            OrganizationalStructAssigmentsErrorDefintion errorDefinition, Object... params) {
        return new OrganizationStructEventErrorException(
                errorDefinition.builder().params(params).build());
    }

    @SuppressWarnings("unchecked")
    private EntityLinkResolver<com.eisgroup.genesis.factory.modeling.types.immutable.Organization> getResolverForLink(
            EntityLink<com.eisgroup.genesis.factory.modeling.types.immutable.Organization> organization) {
        return entityLinkResolverRegistry.getByURIScheme(organization.getSchema());
    }

    private Boolean isNotInEffectiveInterval(LocalDate effectiveDate, LocalDate expirationDate) {
        LocalDate currentDate = LocalDate.now();
        return currentDate.isBefore(effectiveDate) || currentDate.isAfter(expirationDate);
    }

    private OrganizationValidationFailureEvent createFailureEvent(Throwable error, String policyUri) {
        if (error instanceof OrganizationStructEventErrorException) {
            ErrorHolder errorHolder = ((OrganizationStructEventErrorException) error).getErrorHolder();
            return new OrganizationValidationFailureEvent(NAME, policyUri, errorHolder);
        }

        return new OrganizationValidationFailureEvent(NAME, policyUri);
    }


}
