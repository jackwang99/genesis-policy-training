/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.batch.commands.handlers.autoprocessing.quote;

import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.AbstractStrategyJob;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.PolicyJobs;
import com.eisgroup.genesis.factory.modeling.types.JobProcessingStrategy;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.model.Variation;
import com.eisgroup.genesis.policy.core.jps.model.ProcessedStepType;
import com.eisgroup.genesis.policy.core.jps.model.StrategyType;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeRequest;
import com.eisgroup.genesis.policy.core.model.PolicyVariations;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author mkarzhavin
 * @since 10.4
 */
public class QuoteCommonJob extends AbstractStrategyJob {
    @Override
    public String getName() {
        return PolicyJobs.QUOTE_COMMON_JOB;
    }

    @Nonnull
    @Override
    protected StrategyType getStrategyType() {
        return StrategyType.QUOTE_COMMON;
    }

    @Nonnull
    @Override
    protected ProcessedStepType getProcessedStepType() {
        return ProcessedStepType.UPDATE;
    }

    @Nonnull
    @Override
    protected Optional<CompositeRequest> getRequest(JobProcessingStrategy strategy, PolicySummary policySummary) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Variation getVariation() {
        return PolicyVariations.QUOTE;
    }

}
