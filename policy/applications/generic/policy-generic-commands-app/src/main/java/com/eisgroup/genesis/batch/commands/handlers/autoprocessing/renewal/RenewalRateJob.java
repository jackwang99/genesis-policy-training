/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.batch.commands.handlers.autoprocessing.renewal;

import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.PolicyJobs;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.endorsement.EndorsementRateJob;
import com.eisgroup.genesis.factory.modeling.types.JobProcessingStrategy;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.policy.core.jps.model.StrategyType;

import javax.annotation.Nonnull;

/**
 * Batch job which rates automatically generated renewal according to the configuration of {@link JobProcessingStrategy}.
 *
 * @author akozel
 * @since 9.12
 *
 * @see JobProcessingStrategy
 */
public class RenewalRateJob extends EndorsementRateJob {

    @Override
    public String getName() {
        return PolicyJobs.RENEWAL_RATE_JOB;
    }

    @Nonnull
    @Override
    protected StrategyType getStrategyType() {
        return StrategyType.RENEWAL;
    }

    @Override
    protected boolean filterPolicy(@Nonnull JobProcessingStrategy strategy, @Nonnull PolicySummary policySummary) {
        return new BaseRenewalFlagsPredicate().test(policySummary);
    }
}