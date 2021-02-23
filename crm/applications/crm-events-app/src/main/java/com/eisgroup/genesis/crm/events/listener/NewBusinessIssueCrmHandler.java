/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.events.listener;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import com.eisgroup.genesis.communication.ClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eisgroup.genesis.command.Command;
import com.eisgroup.genesis.commands.publisher.api.CommandPublisher;
import com.eisgroup.genesis.commands.request.IdentifierRequest;
import com.eisgroup.genesis.crm.commands.CrmCommands;
import com.eisgroup.genesis.crm.rdf.OpportunityToRootEntity;
import com.eisgroup.genesis.events.EventEnvelope;
import com.eisgroup.genesis.events.handler.StreamEventHandler;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.modeling.types.immutable.Opportunity;
import com.eisgroup.genesis.factory.modeling.types.immutable.PolicySummary;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;
import com.eisgroup.genesis.factory.repository.links.DetachedEntity;
import com.eisgroup.genesis.factory.repository.links.FactoryLink;
import com.eisgroup.genesis.factory.repository.links.VersionRoot;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.json.link.EntityLink;
import com.eisgroup.genesis.json.link.EntityLinkBuilder;
import com.eisgroup.genesis.json.link.EntityLinkBuilderRegistry;
import com.eisgroup.genesis.json.link.LinkingParams;
import com.eisgroup.genesis.lifecycle.events.CommandExecutedEvent;
import com.eisgroup.genesis.model.Variation;
import com.eisgroup.genesis.model.external.ExternalModelRepository;
import com.eisgroup.genesis.model.external.error.ExternalModelNotAvailableException;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.policy.core.model.PolicyVariations;
import com.eisgroup.genesis.rdf.repository.RelationshipRepository;
import com.eisgroup.genesis.rdf.repository.TripletMatcher;
import com.eisgroup.genesis.streams.CommandExecutedMessageMetadata;
import com.eisgroup.genesis.streams.MessageMetadata;
import com.eisgroup.genesis.streams.consumer.MessageRetryContext;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;


/**
 * Listens to command executed events. These events are then filtered by
 * {@link #filterFunction} that is passed through constructor. Actual event does
 * not matter as assumption is made that all events contains URI for policy.
 * Loads {@link Opportunity}s for source policy and send command for closing such opportunities.
 * 
 * @author Dmitry Andronchik
 */
public class NewBusinessIssueCrmHandler extends BaseExternalEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(NewBusinessIssueCrmHandler.class);
    
    private static final String OPPORTUNITY_MODEL_NAME = "Opportunity";

    private final Predicate<String> filterFunction;
    private final EntityLinkBuilderRegistry entityLinkBuilderRegistry;
    private final RelationshipRepository rdfRepo;
    private final CommandPublisher commandPublisher;
    private final ModelRepository<DomainModel> modelRepo;

    public NewBusinessIssueCrmHandler(Predicate<String> filterFunction,
            EntityLinkBuilderRegistry entityLinkBuilderRegistry, RelationshipRepository rdfRepo, 
            CommandPublisher commandPublisher, 
            ExternalModelRepository externalModelRepo) {
        super(filterFunction, externalModelRepo);
        this.filterFunction = filterFunction;
        this.entityLinkBuilderRegistry = entityLinkBuilderRegistry;
        this.rdfRepo = rdfRepo;
        this.commandPublisher = commandPublisher;
        this.modelRepo = externalModelRepo.subscribeAsModelRepository(DomainModel.class);
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
            .filter(entity -> entity instanceof PolicySummary)
            .map(entity -> doHandle((PolicySummary) entity, message))
            .orElse(Completable.complete());
    }

    private Completable doHandle(PolicySummary policy, CommandExecutedEvent message) {
        DetachedEntity policyDTO = new DetachedEntity(policy.getKey(), policy.getModelName(), policy.getModelType(), policy.getModelVersion());
        EntityLinkBuilder<RootEntity> builder = entityLinkBuilderRegistry.getByType(policyDTO.getClass()); 
        EntityLink<RootEntity> rootPolicyLink = builder
                .createLink((RootEntity) policyDTO, 
                        LinkingParams.just(Variation.class, PolicyVariations.QUOTE)
                        .with(VersionRoot.class)
                        .with(DomainModel.class, modelRepo.getActiveModel(policy.getModelName())));
        
        String policyUri = rootPolicyLink.getURIString();
        logger.debug("policyUri: " + policyUri);        
        
        List<com.eisgroup.genesis.rdf.Predicate<Opportunity, RootEntity>> predicates = Collections
                .singletonList(OpportunityToRootEntity.ASSOCIATION);
        TripletMatcher opportunityMather = new TripletMatcher(Opportunity.NAME, predicates, rootPolicyLink);
        TripletMatcher rootOpportunityMatcher = TripletMatcher.createSelfMatcher(Opportunity.NAME, opportunityMather);

        Disposable disposable = rdfRepo.<RootEntity>search(rootOpportunityMatcher)
                .forEach(link -> sendOpportunityCloseCommand(link));
        return Completable.complete();
    }
    
    private void sendOpportunityCloseCommand(EntityLink<RootEntity> opportunityLink) {
        String variation = Variation.INVARIANT.getName();
        FactoryLink factoryLink = new FactoryLink(opportunityLink);
        RootEntityKey opportunityKey = new RootEntityKey(factoryLink.getRootId(), factoryLink.getRevisionNo());
        Command command = new Command(variation, CrmCommands.CLOSE_WON, new IdentifierRequest(opportunityKey).toJson(), false);
        // send command
        commandPublisher.publish(command, OPPORTUNITY_MODEL_NAME, ClientContext.getCurrentInstance());
    }
}
