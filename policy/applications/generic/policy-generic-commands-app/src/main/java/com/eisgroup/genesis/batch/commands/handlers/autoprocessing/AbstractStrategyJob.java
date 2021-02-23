/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.batch.commands.handlers.autoprocessing;

import com.eisgroup.genesis.command.Command;
import com.eisgroup.genesis.factory.json.VariableEntity;
import com.eisgroup.genesis.factory.modeling.types.JobProcessingStrategy;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.factory.modeling.types.immutable.JobProcessingActionConfig;
import com.eisgroup.genesis.factory.modeling.types.immutable.JobProcessingStep;
import com.eisgroup.genesis.jobs.lifecycle.api.commands.output.SubsequentCommand;
import com.eisgroup.genesis.jobs.lifecycle.api.handler.CommandCollectingHandler;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.model.Variation;
import com.eisgroup.genesis.policy.core.jps.model.ExecutionType;
import com.eisgroup.genesis.policy.core.jps.model.JobStrategyUtils;
import com.eisgroup.genesis.policy.core.jps.model.ProcessedStepType;
import com.eisgroup.genesis.policy.core.jps.model.StrategyType;
import com.eisgroup.genesis.policy.core.jps.services.JobProcessingStrategyService;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeEntityKeyRequest;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeRequest;
import io.reactivex.Observable;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Abstract class which contains common logic for batch which use {@link JobProcessingStrategy}
 * for auto processing of policy (endorsement and renewal jobs).
 *
 * @author ileanavets
 * @since 9.12
 *
 * @see JobProcessingStrategy
 */
public abstract class AbstractStrategyJob extends CommandCollectingHandler {

    @Autowired
    private JobProcessingStrategyService jobProcessingStrategyService;

    @Override
    public Observable<SubsequentCommand> execute() {
        Observable<Pair<JobProcessingStrategy, PolicySummary>> selectedPolicy = jobProcessingStrategyService.getActiveStrategies(getStrategyType(), LocalDate.now())
                .flatMap(activeStrategy ->
                        jobProcessingStrategyService.selectPoliciesByStrategy(activeStrategy, getProcessedStepType())
                                .map(policy -> Pair.of(activeStrategy, policy)))
                .filter(strategyPolicyPair -> filterPolicy(strategyPolicyPair.getLeft(), strategyPolicyPair.getRight()));
        return transform(selectedPolicy)
                .flatMap(strategyPolicyPair -> toSubsequentCommand(strategyPolicyPair.getLeft(), strategyPolicyPair.getRight()));
    }

    protected Observable<Pair<JobProcessingStrategy, PolicySummary>> transform(Observable<Pair<JobProcessingStrategy, PolicySummary>> selectedPolicy) {
        return selectedPolicy;
    }

    protected Observable<SubsequentCommand> toSubsequentCommand(JobProcessingStrategy strategy, PolicySummary policy) {
        final CompositeRequest.Builder requestBuilder = new CompositeRequest.Builder();

        addActionsToRequest(requestBuilder, strategy, policy, ExecutionType.BEFORE);

        getPreRequest(strategy, policy)
                .ifPresent(requestBuilder::addNextRequest);

        getRequest(strategy, policy)
                .ifPresent(requestBuilder::addNextRequest);

        getPostRequest(strategy, policy)
                .ifPresent(requestBuilder::addNextRequest);

        addActionsToRequest(requestBuilder, strategy, policy, ExecutionType.AFTER);

        final CompositeRequest request = requestBuilder.build();

        Command command = new Command(request.getVariation(), request.getCommandName(), request.toJson());

        return Observable.just(new SubsequentCommand(policy.getModelName(), command));
    }

    /**
     * Filter policy if required. By default all policy is passed.
     *
     * @param strategy job strategy
     * @param policySummary policy to filter
     * @return true - if policy should passed to processing, false - should be filtered.
     */
    protected boolean filterPolicy(@Nonnull JobProcessingStrategy strategy, @Nonnull PolicySummary policySummary) {
        return true;
    }

    @Nonnull
    protected abstract StrategyType getStrategyType();

    @Nonnull
    protected abstract ProcessedStepType getProcessedStepType();

    /**
     * Provide pre request which should be executed before main command ({@link AbstractStrategyJob#getRequest(JobProcessingStrategy, PolicySummary)}.
     *
     * @param strategy job strategy
     * @param policySummary policy to auto process
     * @return pre request
     */
    protected Optional<CompositeRequest> getPreRequest(JobProcessingStrategy strategy, PolicySummary policySummary) {
        return Optional.empty();
    }

    /**
     * Provide request which should auto process passed policy.
     *
     * @param strategy job strategy
     * @param policySummary policy to auto process
     * @return request
     */
    protected abstract Optional<CompositeRequest> getRequest(JobProcessingStrategy strategy, PolicySummary policySummary);

    /**
     * Provide post request which should be executed after main command ({@link AbstractStrategyJob#getRequest(JobProcessingStrategy, PolicySummary)}.
     *
     * @param strategy job strategy
     * @param policySummary policy to auto process
     * @return pre request
     */
    protected Optional<CompositeRequest> getPostRequest(JobProcessingStrategy strategy, PolicySummary policySummary) {
        return Optional.empty();
    }

    private void addActionsToRequest(CompositeRequest.Builder requestBuilder,
                                     JobProcessingStrategy strategy, PolicySummary policy, ExecutionType executionType) {
        final RootEntityKey rootEntityKey = policy.getKey();
        final String variationName = ((VariableEntity) policy).getVariation().getName();

        resolveActions(strategy, executionType)
                .forEach(action -> requestBuilder.addNextRequest(new CompositeEntityKeyRequest(rootEntityKey, action, variationName)));
    }

    private List<String> resolveActions(JobProcessingStrategy strategy, ExecutionType executionType) {
        return JobStrategyUtils.resolveStepByType(strategy, getProcessedStepType())
                .map(JobProcessingStep::getActions)
                .orElse(Collections.emptyList())
                .stream()
                .filter(action -> executionType.name().equals(action.getExecutionType()))
                .map(JobProcessingActionConfig::getActionName)
                .collect(Collectors.toList());
    }

    protected boolean isKindOfPolicyVariation(PolicySummary policy, Variation variation) {
        return variation.getName().equals(((VariableEntity) policy).getVariation().getName());
    }

    protected JobProcessingStrategyService getJobProcessingStrategyService() {
        return jobProcessingStrategyService;
    }
}