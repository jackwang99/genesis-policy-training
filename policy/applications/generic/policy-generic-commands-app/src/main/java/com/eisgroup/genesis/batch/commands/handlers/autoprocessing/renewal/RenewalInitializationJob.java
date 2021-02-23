/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.batch.commands.handlers.autoprocessing.renewal;

import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.AbstractStrategyJob;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.PolicyJobs;
import com.eisgroup.genesis.factory.modeling.types.JobProcessingStrategy;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.policy.core.jps.model.ProcessedStepType;
import com.eisgroup.genesis.policy.core.jps.model.StrategyType;
import com.eisgroup.genesis.policy.core.lifecycle.commands.PolicyCommands;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeEntityKeyRequest;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeRequest;
import com.eisgroup.genesis.policy.core.model.PolicyVariations;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Batch job which makes an renewal initiation of policies according to the configuration of {@link JobProcessingStrategy}.
 *
 * @author akozel
 * @since 9.12
 */
public class RenewalInitializationJob extends AbstractStrategyJob {

    @Override
    public String getName() {
        return PolicyJobs.RENEWAL_INIT_JOB;
    }

    @Nonnull
    @Override
    protected StrategyType getStrategyType() {
        return StrategyType.RENEWAL;
    }

    @Nonnull
    @Override
    protected ProcessedStepType getProcessedStepType() {
        return ProcessedStepType.INIT;
    }

    @Nonnull
    @Override
    protected Optional<CompositeRequest> getRequest(JobProcessingStrategy strategy, PolicySummary policySummary) {
        return Optional.of(new CompositeEntityKeyRequest(new RootEntityKey(policySummary.getKey().getRootId(), policySummary.getKey().getRevisionNo()),
                PolicyCommands.RENEW, PolicyVariations.POLICY.getName()));
    }

    @Override
    protected boolean filterPolicy(@Nonnull JobProcessingStrategy strategy, @Nonnull PolicySummary policySummary) {
        return new BaseRenewalFlagsPredicate().test(policySummary);
    }
}