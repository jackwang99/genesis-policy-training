/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.batch.commands.handlers.autoprocessing;

import com.eisgroup.genesis.factory.modeling.types.JobProcessingStrategy;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.policy.core.jps.model.ProcessedStepType;
import com.eisgroup.genesis.policy.core.jps.model.StrategyType;
import com.eisgroup.genesis.policy.core.lifecycle.commands.PolicyCommands;
import com.eisgroup.genesis.policy.core.lifecycle.commands.QuoteCommands;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeEntityKeyRequest;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeRequest;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeRootIdentifierRequest;
import com.eisgroup.genesis.policy.core.model.PolicyVariations;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author yratkevich
 * @since 9.15
 */
public class ArchivePolicyJob extends AbstractArchivePolicyJob {

    @Override
    public String getName() {
        return PolicyJobs.ARCHIVE_JOB;
    }

    @Nonnull
    @Override
    protected StrategyType getStrategyType() {
        return StrategyType.ARCHIVE;
    }

    @Nonnull
    @Override
    protected ProcessedStepType getProcessedStepType() {
        return ProcessedStepType.INIT;
    }

    @Nonnull
    @Override
    protected Optional<CompositeRequest> getRequest(JobProcessingStrategy strategy, PolicySummary policySummary) {
        if (isQuote(policySummary)) {
            return Optional.of(new CompositeRequest.Builder()
                    .addNextRequest(new CompositeEntityKeyRequest(policySummary.getKey()))
                    .withCommandName(QuoteCommands.ARCHIVE)
                    .withVariation(PolicyVariations.QUOTE.getName())
                    .build());
        } else if (isPolicy(policySummary)) {
            return Optional.of(new CompositeRequest.Builder()
                    .addNextRequest(new CompositeRootIdentifierRequest(policySummary.getKey().getRootId()))
                    .withCommandName(PolicyCommands.ARCHIVE)
                    .withVariation(PolicyVariations.POLICY.getName())
                    .build());
        } else {
            throw new IllegalArgumentException("Unsupported variation for an archive job");
        }
    }

    @Override
    protected boolean isPolicy(PolicySummary policy) {
        return isKindOfPolicyVariation(policy, PolicyVariations.POLICY);
    }

    @Override
    protected boolean isQuote(PolicySummary policy) {
        return isKindOfPolicyVariation(policy, PolicyVariations.QUOTE);
    }
}