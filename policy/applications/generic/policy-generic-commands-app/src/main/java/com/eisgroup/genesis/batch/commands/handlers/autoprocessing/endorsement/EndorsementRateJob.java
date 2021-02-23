/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.batch.commands.handlers.autoprocessing.endorsement;

import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.AbstractStrategyJob;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.PolicyJobs;
import com.eisgroup.genesis.factory.modeling.types.EndorseRenewJobProcessingRule;
import com.eisgroup.genesis.factory.modeling.types.JobProcessingStrategy;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.factory.modeling.types.immutable.JobStrategyDetails;
import com.eisgroup.genesis.policy.core.jps.model.ProcessedStepType;
import com.eisgroup.genesis.policy.core.jps.model.StrategyType;
import com.eisgroup.genesis.policy.core.lifecycle.commands.QuoteCommands;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeEntityKeyRequest;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeRequest;
import com.eisgroup.genesis.policy.core.model.PolicyVariations;
import org.apache.commons.lang3.BooleanUtils;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Batch job which rates automatically generated endorsements according to the configuration of {@link JobProcessingStrategy}.
 *
 * @author ileanavets
 * @since 9.12
 *
 * @see JobProcessingStrategy
 */
public class EndorsementRateJob extends AbstractStrategyJob {

    @Override
    public String getName() {
        return PolicyJobs.ENDORSEMENT_RATE_JOB;
    }

    @Nonnull
    @Override
    protected StrategyType getStrategyType() {
        return StrategyType.ENDORSEMENT;
    }

    @Nonnull
    @Override
    protected ProcessedStepType getProcessedStepType() {
        return ProcessedStepType.RATE;
    }

    @Nonnull
    @Override
    protected Optional<CompositeRequest> getRequest(JobProcessingStrategy strategy, PolicySummary policySummary) {
        final String commandName = Optional.of(strategy)
                .map(JobProcessingStrategy::getDetails)
                .map(JobStrategyDetails::getRule)
                .filter(EndorseRenewJobProcessingRule.class::isInstance)
                .map(EndorseRenewJobProcessingRule.class::cast)
                .map(EndorseRenewJobProcessingRule::getRatePolicyInd)
                .filter(BooleanUtils::isTrue)
                .map(rateInd -> QuoteCommands.RATE)
                .orElse(QuoteCommands.PRORATE);

        return Optional.of(new CompositeEntityKeyRequest(policySummary.getKey(), commandName, PolicyVariations.QUOTE.getName()));
    }

}
