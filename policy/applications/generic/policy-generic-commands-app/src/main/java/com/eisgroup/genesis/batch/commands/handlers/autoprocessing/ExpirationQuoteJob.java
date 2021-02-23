/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.batch.commands.handlers.autoprocessing;

import com.eisgroup.genesis.factory.modeling.types.JobProcessingStrategy;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.model.Variation;
import com.eisgroup.genesis.policy.core.jps.model.ProcessedStepType;
import com.eisgroup.genesis.policy.core.jps.model.StrategyType;
import com.eisgroup.genesis.policy.core.lifecycle.commands.QuoteCommands;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeEntityKeyRequest;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeRequest;
import com.eisgroup.genesis.policy.core.model.PolicyVariations;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author yratkevich
 * @since 9.15
 */
public class ExpirationQuoteJob extends AbstractStrategyJob {

    @Override
    public String getName() {
        return PolicyJobs.EXPIRATION_QUOTE_JOB;
    }

    @Nonnull
    @Override
    public Variation getVariation() {
        return PolicyVariations.QUOTE;
    }

    @Nonnull
    @Override
    protected StrategyType getStrategyType() {
        return StrategyType.EXPIRATION;
    }

    @Nonnull
    @Override
    protected ProcessedStepType getProcessedStepType() {
        return ProcessedStepType.INIT;
    }

    @Nonnull
    @Override
    protected Optional<CompositeRequest> getRequest(JobProcessingStrategy strategy, PolicySummary policySummary) {
        return Optional.of(new CompositeEntityKeyRequest(policySummary.getKey(), QuoteCommands.EXPIRE, PolicyVariations.QUOTE.getName()));
    }

}
