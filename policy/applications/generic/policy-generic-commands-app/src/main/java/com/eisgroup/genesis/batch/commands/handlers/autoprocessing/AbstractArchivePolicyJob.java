/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.batch.commands.handlers.autoprocessing;

import com.eisgroup.genesis.factory.modeling.types.JobProcessingStrategy;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import io.reactivex.Observable;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contains common logic for archiving jobs
 *
 * @author ileanavets
 * @since 10.1
 */
abstract class AbstractArchivePolicyJob extends AbstractStrategyJob {

    @Override
    protected Observable<Pair<JobProcessingStrategy, PolicySummary>> transform(Observable<Pair<JobProcessingStrategy, PolicySummary>> selectedPolicy) {
        //filters policies to have only one version (with same rootId) as policies are archived all versions at a time
        Set<UUID> processedPolicies = ConcurrentHashMap.newKeySet();
        return selectedPolicy
                .filter(strategyPolicyPair -> isQuote(strategyPolicyPair.getRight())
                        || processedPolicies.add(strategyPolicyPair.getRight().getKey().getRootId()));
    }

    protected abstract boolean isPolicy(PolicySummary policy);

    protected abstract boolean isQuote(PolicySummary policy);

}
