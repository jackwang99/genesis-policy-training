/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.packaging.offer.listener;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.factory.modeling.types.TransactionDetails;
import com.eisgroup.genesis.factory.utils.selector.JsonNode;
import com.eisgroup.genesis.json.JsonEntity;
import com.eisgroup.genesis.json.wrapper.JsonWrapperFactory;
import com.eisgroup.genesis.json.wrapper.JsonWrapperFactoryProvider;
import com.eisgroup.genesis.packaging.UserOperation;

import io.reactivex.Single;

/**
 * Grandfather Offer Manager listener
 * 
 * @author aspichakou
 *
 */
public class GrandfatherOfferManagerListener implements OfferManagerExecutorListener<JsonEntity> {

    private JsonWrapperFactory wrapperFactory = JsonWrapperFactoryProvider.getJsonWrapperFactory();

    @Autowired
    private GfCoverageCollector collector;

    @Autowired
    private GfCoveragePusher pusher;

    @Override
    public String getName() {
        return "grandfather";
    }

    @Override
    public Single<JsonEntity> execute(JsonEntity originalEntity, JsonEntity entity, String entryPoint, UserOperation operation) {
        final Single<JsonEntity> mutated = execute(entity, entryPoint, operation);

        final PolicySummary policy = wrapperFactory.wrap(entity.toJson(), PolicySummary.class);
        final TransactionDetails transactionDetails = policy.getTransactionDetails();
        final LocalDate txEffective = transactionDetails.getTxEffectiveDate().toLocalDate();

        return mutated.map(m -> {
            final List<JsonNode> gfEntities = collector.getGfEntities(originalEntity.toJson(), txEffective);
            pusher.pushGfEntitites(m.toJson(), gfEntities);
            return m;
        });
    }

    @Override
    public Single<JsonEntity> execute(JsonEntity entity, String entryPoint, UserOperation operation) {
        return Single.just(entity);
    }

}
