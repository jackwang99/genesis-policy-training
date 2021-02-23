/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.batch.commands.handlers.autoprocessing.endorsement;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.Optional;

import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.AbstractStrategyJob;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.PolicyJobs;
import com.eisgroup.genesis.factory.modeling.types.EndorsementJobStrategyDetails;
import com.eisgroup.genesis.factory.modeling.types.JobProcessingStrategy;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.policy.core.jps.model.PendingTransactionsHandlingType;
import com.eisgroup.genesis.policy.core.jps.model.ProcessedStepType;
import com.eisgroup.genesis.policy.core.jps.model.StrategyType;
import com.eisgroup.genesis.policy.core.lifecycle.commands.AutoProcessingCommands;
import com.eisgroup.genesis.policy.core.lifecycle.commands.PolicyCommands;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeRequest;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.EndorseRequest;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.autoprocessing.PreAutomatedEndorseRequest;
import com.eisgroup.genesis.policy.core.model.PolicyVariations;

/**
 * Batch job which makes an endorsement initiation of policies according to the configuration of {@link JobProcessingStrategy}.
 *
 * @author ileanavets
 * @since 9.12
 */
public class EndorsementInitiationJob extends AbstractStrategyJob {

    @Override
    public String getName() {
        return PolicyJobs.ENDORSEMENT_INIT_JOB;
    }

    @Nonnull
    @Override
    protected StrategyType getStrategyType() {
        return StrategyType.ENDORSEMENT;
    }

    @Nonnull
    @Override
    protected ProcessedStepType getProcessedStepType() {
        return ProcessedStepType.INIT;
    }

    @Override
    protected Optional<CompositeRequest> getPreRequest(JobProcessingStrategy strategy, PolicySummary policySummary) {
        return Optional.of(new CompositeRequest.Builder().addNextRequest(
                new PreAutomatedEndorseRequest(policySummary.getKey(),
                        LocalDate.now().atStartOfDay(),
                        isDeletePendingTransactions(strategy)))
                .withCommandName(AutoProcessingCommands.PRE_AUTOMATED_ENDORSE)
                .withVariation(PolicyVariations.POLICY.getName())
                .build());
    }

    private boolean isDeletePendingTransactions(JobProcessingStrategy strategy) {
        return PendingTransactionsHandlingType.DELETE_PENDING_TRANSACTIONS.name().equals(
                ((EndorsementJobStrategyDetails)strategy.getDetails()).getRule().getPendingTransactionsHandling());
    }

    @Nonnull
    @Override
    protected Optional<CompositeRequest> getRequest(JobProcessingStrategy strategy, PolicySummary policySummary) {
        return Optional.of(new CompositeRequest.Builder().addNextRequest(
                new EndorseRequest(
                        new RootEntityKey(policySummary.getKey().getRootId(), policySummary.getKey().getRevisionNo()),
                        LocalDate.now().atStartOfDay(), null, null))
                .withCommandName(PolicyCommands.ENDORSE)
                .withVariation(PolicyVariations.POLICY.getName())
                .build());
    }
}
