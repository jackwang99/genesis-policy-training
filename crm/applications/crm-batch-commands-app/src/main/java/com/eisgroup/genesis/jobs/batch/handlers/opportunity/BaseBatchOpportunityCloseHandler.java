/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.jobs.batch.handlers.opportunity;

import com.eisgroup.genesis.commands.request.IdentifierRequest;
import com.eisgroup.genesis.criteria.Matcher;
import com.eisgroup.genesis.facade.config.PagingConfig;
import com.eisgroup.genesis.facade.search.ModeledEntitySearchCriteria;
import com.eisgroup.genesis.facade.search.ModeledEntitySearchRepository;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.modeling.types.Opportunity;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;
import com.eisgroup.genesis.factory.repository.ModeledEntitySchemaResolver;
import com.eisgroup.genesis.jobs.lifecycle.api.commands.output.SubsequentCommand;
import com.eisgroup.genesis.json.key.BaseKey;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.model.Variation;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import com.eisgroup.genesis.search.SearchIndexQuery;
import com.google.common.collect.Sets;
import io.reactivex.Observable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Dmitry Andronchik
 */
public abstract class BaseBatchOpportunityCloseHandler extends BaseBatchOpportunityCommandHandler {

    protected static final Set<String> STATES_TO_CLOSE = Sets.newHashSet("draft", "inPipeline", "quoted");
    
    protected static final String UPDATED_ON = "updatedOn"; 
    protected static final String STATE = "state";
    
    protected static final PagingConfig pagingConfig = PagingConfig.getInstance();
    
    protected ModelRepository<DomainModel> modelRepo = ModelRepositoryFactory.getRepositoryFor(DomainModel.class);
    
    @Autowired
    protected ModeledEntitySearchRepository searchRepo;    
    
    protected Observable<SubsequentCommand> execute(Integer daysWithoutChanges) {
        return searchRepo.search(parseCriteria(daysWithoutChanges, 1, 0))
                .getResultCount()
                .toObservable()
                .flatMap(count -> {
                    Observable<RootEntity> result = Observable.empty();
                    for (int offset = 0; offset < count; offset += pagingConfig.getHardLimit()) {
                        result = result.concatWith(
                                searchRepo.search(parseCriteria(daysWithoutChanges, pagingConfig.getHardLimit(), offset)).getResults()
                        );
                    }
                    return result;
                })
                .map(opportunity -> new SubsequentCommand(getCommandNameToBeExecuted(),
                                                   new IdentifierRequest(new RootEntityKey(opportunity.getKey().getRootId(), opportunity.getKey().getRevisionNo())),
                                                   Variation.INVARIANT,
                                                   OPPORTUNITY_MODEL_NAME));
    }
    
    protected ModeledEntitySearchCriteria parseCriteria(Integer daysWithoutChanges, Integer limit, Integer offset) {
        DomainModel model = modelRepo.getActiveModel(Opportunity.NAME);
        String searchSchemaName = ModeledEntitySchemaResolver.getSearchSchemaNameUsing(model, Variation.INVARIANT);

        Collection<Matcher> searchMatchers = Arrays.asList(
                new SearchIndexQuery.FieldMatcher(UPDATED_ON, SearchIndexQuery.FieldMatcher.Operand.lte, Arrays.asList(LocalDate.now().minusDays(daysWithoutChanges))),
                new SearchIndexQuery.FieldMatcher(STATE, STATES_TO_CLOSE)
        );

        return new ModeledEntitySearchCriteria(searchSchemaName, searchMatchers, null,
                Collections.singleton(BaseKey.ROOT_ID), limit, offset);
    }    
}
