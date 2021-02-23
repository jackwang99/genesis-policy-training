/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization;

import com.eisgroup.genesis.decision.DecisionService;
import com.eisgroup.genesis.decision.dimension.DecisionContextBuildHelper;
import com.eisgroup.genesis.factory.model.RulesModel;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.factory.utils.BusinessModelJsonTraverser;
import com.eisgroup.genesis.json.wrapper.JsonWrapperFactory;
import com.eisgroup.genesis.json.wrapper.JsonWrapperFactoryProvider;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.aspects.ImpactDescriptor;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.observers.ImpactObserver;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.observers.processors.ImpactProcessor;
import com.google.gson.JsonObject;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

/**
 * @author sbelauski
 */
public class DefaultOfferImpactResolver implements OfferImpactResolver {

    private static final String IMPACT = "impact";
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOfferImpactResolver.class);
    private final BusinessModelJsonTraverser traverser;
    private final JsonWrapperFactory wrapperFactory = JsonWrapperFactoryProvider.getJsonWrapperFactory();
    private final DecisionService decisionService;
    private final ModelResolver modelResolver;
    private final List<ImpactProcessor> impactProcessors;
    private final RulesModel rulesModel;

    @Autowired
    public DefaultOfferImpactResolver(DecisionService decisionService,
                                      ModelResolver modelResolver,
                                      List<ImpactProcessor> impactProcessors,
                                      RulesModel rulesModel,
                                      BusinessModelJsonTraverser traverser) {
        this.decisionService = decisionService;
        this.modelResolver = modelResolver;
        this.impactProcessors = impactProcessors;
        this.rulesModel = rulesModel;
        this.traverser = traverser;
    }

    @Override
    public Single<PolicySummary> applyDiffs(List<OfferImpactDiffHolder> attributeDiffs, PolicySummary targetQuote) {
        LOGGER.warn("Applying diffs: {}, to target: {}", attributeDiffs, targetQuote);
        if (CollectionUtils.isEmpty(attributeDiffs)) {
            LOGGER.warn("Diffs are empty");
            return Single.just(targetQuote);
        }

        final JsonObject targetJsonCopy = targetQuote.toJson().deepCopy();

        return collectImpactDescriptors(attributeDiffs)
                .map(impactDescriptors -> new ImpactObserver(impactDescriptors, impactProcessors))
                .doOnSuccess(impactObserver -> {
                    LOGGER.warn("Using impact observer: {}, traversing target: {}", impactObserver, targetJsonCopy);
                    traverser.traverse(targetJsonCopy, modelResolver.resolveModel(DomainModel.class), impactObserver);
                })
                .map(o -> wrapperFactory.wrap(targetJsonCopy, PolicySummary.class));

    }

    private Single<List<ImpactDescriptor>> collectImpactDescriptors(List<OfferImpactDiffHolder> attributeDiffs) {
        LOGGER.warn("Collecting impact descriptors");
        return attributeDiffs.stream()
                .map(diff -> Pair.of(diff.getAttribute(), diff.getDiff().getPath()))
                .map(pair -> Pair.of(Collections.singletonMap(IMPACT, (Object) pair.getLeft()), pair.getRight()))
                .map(pair -> Pair.of(DecisionContextBuildHelper.addStaticContextAndDimensionsFrom(pair.getLeft()).build(), pair.getRight()))
                .map(pair -> decisionService.evaluateTable(ImpactDescriptor.IMPACT_TABLE_NAME, rulesModel, pair.getLeft())
                        .map(rowResult -> new ImpactDescriptor(rowResult, pair.getRight())))
                .reduce(Observable.empty(), Observable::concatWith)
                .toList();
    }

}