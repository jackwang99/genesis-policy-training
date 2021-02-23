/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization;

import com.eisgroup.genesis.ModelContext;
import com.eisgroup.genesis.decision.DecisionService;
import com.eisgroup.genesis.factory.model.RulesModel;
import com.eisgroup.genesis.factory.utils.BusinessModelJsonTraverser;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.observers.processors.ImpactMoneyProcessor;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.observers.processors.ImpactProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author yratkevich
 * @since 9.16
 */
@Configuration
public class PersonalAutoOfferImpactConfig {

    @Bean
    public OfferImpactResolver noOpOfferImpactResolver() {
        return new NoOpOfferImpactResolver();
    }

    @Bean
    public OfferImpactResolver defaultOfferImpactResolver(DecisionService decisionService,
                                                          ModelResolver modelResolver,
                                                          List<ImpactProcessor> impactProcessors) {

        final RulesModel rulesModel = ModelRepositoryFactory.getRepositoryFor(RulesModel.class)
                .getActiveModel(ModelContext.getCurrentInstance().getModelName());

        return new DefaultOfferImpactResolver(decisionService, modelResolver, impactProcessors, rulesModel, new BusinessModelJsonTraverser());
    }

    @Bean
    public ImpactProcessor impactMoneyProcessor() {
        return new ImpactMoneyProcessor();
    }
}
