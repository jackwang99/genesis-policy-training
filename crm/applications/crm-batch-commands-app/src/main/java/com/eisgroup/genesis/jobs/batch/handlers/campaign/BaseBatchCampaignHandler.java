/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.jobs.batch.handlers.campaign;

import com.eisgroup.genesis.facade.config.PagingConfig;
import com.eisgroup.genesis.facade.search.ModeledEntitySearchCriteria;
import com.eisgroup.genesis.facade.search.ModeledEntitySearchRepository;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;
import com.eisgroup.genesis.jobs.lifecycle.api.handler.CommandCollectingHandler;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import io.reactivex.Observable;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Valeriy Sizonenko
 */
public abstract class BaseBatchCampaignHandler extends CommandCollectingHandler {

    protected static final PagingConfig pagingConfig = PagingConfig.getInstance();

    protected static final String STATE = "state";

    protected ModelRepository<DomainModel> modelRepo = ModelRepositoryFactory.getRepositoryFor(DomainModel.class);

    @Autowired
    protected ModeledEntitySearchRepository searchRepo;

    protected Observable<RootEntity> searchEntities() {
        return searchRepo.search(parseCriteria(1, 0))
                .getResultCount()
                .toObservable()
                .flatMap(count -> {
                    Observable<RootEntity> result = Observable.empty();
                    for (int offset = 0; offset < count; offset += pagingConfig.getHardLimit()) {
                        result = result.concatWith(
                                searchRepo.search(parseCriteria(pagingConfig.getHardLimit(), offset)).getResults()
                        );
                    }
                    return result;
                });
    }

    protected abstract ModeledEntitySearchCriteria parseCriteria(Integer limit, Integer offset);
}