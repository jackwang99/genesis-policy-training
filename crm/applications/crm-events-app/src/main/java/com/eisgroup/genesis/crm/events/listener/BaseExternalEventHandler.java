/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.events.listener;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.model.external.ExternalModelRepository;
import com.eisgroup.genesis.model.repo.ModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eisgroup.genesis.events.EventEnvelope;
import com.eisgroup.genesis.events.handler.StreamEventHandler;
import com.eisgroup.genesis.lifecycle.events.CommandExecutedEvent;
import com.eisgroup.genesis.model.external.error.ExternalModelNotAvailableException;
import com.eisgroup.genesis.streams.CommandExecutedMessageMetadata;
import com.eisgroup.genesis.streams.MessageMetadata;
import com.eisgroup.genesis.streams.consumer.MessageRetryContext;

import io.reactivex.Completable;

/**
 * 
 * @author Dmitry Andronchik
 * @since 9.13
 */
public abstract class BaseExternalEventHandler implements StreamEventHandler<CommandExecutedEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(BaseExternalEventHandler.class);
    
    protected final Predicate<String> filterFunction;
    protected final ExternalModelRepository externalModelRepo;
    protected ModelRepository<DomainModel> modelRepo;
    
    public BaseExternalEventHandler(Predicate<String> filterFunction, ExternalModelRepository externalModelRepo) {
        this.filterFunction = filterFunction;
        this.externalModelRepo = externalModelRepo;
        this.modelRepo = externalModelRepo.subscribeAsModelRepository(DomainModel.class);
    }
    
    @Override
    public boolean supports(MessageMetadata message) {
        return Optional.ofNullable(message)
            .filter(messageMetadata -> messageMetadata instanceof CommandExecutedMessageMetadata)
            .map(CommandExecutedMessageMetadata.class::cast)
            .map(CommandExecutedMessageMetadata::getCommandName)
            .map(commandName -> filterFunction.test(commandName))
            .orElse(false);
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

    protected void logDomainModels(CommandExecutedMessageMetadata metadata) {
        Collection<String> allModels = modelRepo.getAllModels();
        String allModelsStr = String.join(", ", allModels);

        logger.warn("#18158: All DomainModels size: " + allModels.size());
        logger.warn("#18158: All available DomainModels: " + allModelsStr);

        final String modelName = metadata.getModelName();

        DomainModel domainModel = checkDomainModel(modelName);
        if (domainModel == null) {
            logger.warn("#18158: Try to resubscribe for DomainModel repo");
            this.modelRepo = externalModelRepo.subscribeAsModelRepository(DomainModel.class);
            checkDomainModel(modelName);
        }
    }

    protected DomainModel checkDomainModel(String modelName) {
        try {
            DomainModel domainModel = modelRepo.getActiveModel(modelName);
            logger.warn("#18158: FOUND successfully DomainModel with name: " + modelName);
            return domainModel;
        } catch (ExternalModelNotAvailableException ex) {
            logger.warn("#18158: NOT FOUND DomainModel with name: " + modelName);
            return null;
        }
    }
}
