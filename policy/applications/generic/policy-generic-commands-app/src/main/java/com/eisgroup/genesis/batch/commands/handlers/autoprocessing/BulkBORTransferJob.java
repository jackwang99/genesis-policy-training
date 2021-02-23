/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.batch.commands.handlers.autoprocessing;

import com.eisgroup.genesis.factory.modeling.types.BORTransferJobProcessingRule;
import com.eisgroup.genesis.factory.modeling.types.BORTransferJobStrategyDetails;
import com.eisgroup.genesis.factory.modeling.types.BORTransferPoliciesSelectionCriteria;
import com.eisgroup.genesis.factory.modeling.types.JobProcessingStrategy;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.policy.core.jps.model.ProcessedStepType;
import com.eisgroup.genesis.policy.core.jps.model.StrategyType;
import com.eisgroup.genesis.policy.core.lifecycle.commands.PolicyCommands;
import com.eisgroup.genesis.policy.core.lifecycle.commands.QuoteCommands;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeRequest;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.SingleBORTransferOfOptionRequest;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.PolicySingleBORTransferRequest;
import com.eisgroup.genesis.policy.core.model.PolicyVariations;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author ileanavets
 * @since 9.14
 */
public class BulkBORTransferJob extends AbstractStrategyJob {

    @Override
    public String getName() {
        return PolicyJobs.POLICY_BOR_TRANSFER_JOB;
    }

    @Nonnull
    @Override
    protected StrategyType getStrategyType() {
        return StrategyType.BOR_TRANSFER;
    }

    @Nonnull
    @Override
    protected ProcessedStepType getProcessedStepType() {
        return ProcessedStepType.INIT;
    }

    @Nonnull
    @Override
    protected Optional<CompositeRequest> getRequest(JobProcessingStrategy strategy, PolicySummary policySummary) {
        if (isPolicy(policySummary)) {
            return Optional.of(new CompositeRequest.Builder().addNextRequest(
                    new PolicySingleBORTransferRequest(policySummary.getKey(), strategy.getEffectiveDate().atStartOfDay(),
                            getTxReason(strategy), null, getAgency(strategy), getAgent(strategy), isProcessOnRenewal(strategy)))
                    .withCommandName(PolicyCommands.BOR_TRANSFER)
                    .withVariation(PolicyVariations.POLICY.getName())
                    .build());
        } else if (isQuote(policySummary)) {
            return Optional.of(new CompositeRequest.Builder().addNextRequest(
                    new SingleBORTransferOfOptionRequest(policySummary.getKey(), strategy.getEffectiveDate().atStartOfDay(),
                            getTxReason(strategy), null, getAgency(strategy), getAgent(strategy), isTransferFullOption(strategy)))
                    .withCommandName(QuoteCommands.BOR_TRANSFER)
                    .withVariation(PolicyVariations.QUOTE.getName())
                    .build());
        } else {
            throw new IllegalArgumentException("Unsupported variation for an BOR transfer job");
        }
    }

    private Boolean isTransferFullOption(JobProcessingStrategy strategy) {
        return ((BORTransferPoliciesSelectionCriteria) strategy.getDetails().getPoliciesSelectionCriteria()).getTransferQuotesByOptions();
    }

    private boolean isPolicy(PolicySummary policy) {
        return isKindOfPolicyVariation(policy, PolicyVariations.POLICY);
    }

    private boolean isQuote(PolicySummary policy) {
        return isKindOfPolicyVariation(policy, PolicyVariations.QUOTE);
    }

    private String getAgent(JobProcessingStrategy strategy) {
        return resolveStrategyRuleValue(strategy, BORTransferJobProcessingRule::getTargetAgent);
    }

    private String getAgency(JobProcessingStrategy strategy) {
        return resolveStrategyRuleValue(strategy, BORTransferJobProcessingRule::getTargetAgency);
    }

    private String getTxReason(JobProcessingStrategy strategy) {
        return resolveStrategyRuleValue(strategy, BORTransferJobProcessingRule::getReason);
    }

    private boolean isProcessOnRenewal(JobProcessingStrategy strategy) {
        return Optional.ofNullable(resolveStrategyRuleValue(strategy, BORTransferJobProcessingRule::getProcessOnRenewal))
                .orElse(false);
    }

    private <T> T resolveStrategyRuleValue(JobProcessingStrategy strategy, Function<BORTransferJobProcessingRule, T> valueMapper) {
        return Optional.of(strategy)
                .map(JobProcessingStrategy::getDetails)
                .map(BORTransferJobStrategyDetails.class::cast)
                .map(BORTransferJobStrategyDetails::getRule)
                .map(valueMapper)
                .orElse(null);
    }
}