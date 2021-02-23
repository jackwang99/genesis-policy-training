/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.container.search;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.eisgroup.genesis.crm.core.service.AgencyContainerService;
import com.eisgroup.genesis.factory.json.IdentifiableEntity;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.repository.ModeledEntitySchemaResolver;
import com.eisgroup.genesis.model.Variation;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import com.eisgroup.genesis.search.SearchFieldValue;
import com.eisgroup.genesis.search.SearchIndexDocument;
import com.eisgroup.genesis.search.SearchIndexStatementFactory;
import com.eisgroup.genesis.search.plugin.ModeledSearchIndexPlugin;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

import io.reactivex.Observable;

public class AgencyContainerIndexPlugin implements ModeledSearchIndexPlugin {
    
    private static final ModelRepository<DomainModel> modelRepository = ModelRepositoryFactory.getRepositoryFor(DomainModel.class);
    
    private static final Set<String> SKIPPED_FIELDS = Sets.newHashSet("rootId", "createdOn", "updatedOn");
    private static final String SYSTEM_FIELD_PREFIX = "_";    
    
    private final String customerModelName;
    private final String containerModelName;    
    
    private final SearchIndexStatementFactory statementFactory;
    private final AgencyContainerService agencyContainerService;    

    
    public AgencyContainerIndexPlugin(String customerModelName, String containerModelName, SearchIndexStatementFactory statementFactory, 
                    AgencyContainerService agencyContainerService) {
        this.customerModelName = customerModelName;
        this.containerModelName = containerModelName;
        this.statementFactory = statementFactory;
        this.agencyContainerService = agencyContainerService;
    }

    @Override
    public Observable<Pair<String, Object>> resolveAdditionalFields(DomainModel model, IdentifiableEntity entity) {
        if(!StringUtils.equals(model.getName(), customerModelName)) {
            return Observable.empty();
        }
        
        Observable<JsonObject> containers = agencyContainerService.fetchContainers(entity.toJson());
        
        DomainModel containerModel = modelRepository.getActiveModel(containerModelName);
        String searchSchema = ModeledEntitySchemaResolver.getSearchSchemaNameUsing(containerModel, Variation.INVARIANT);
        
        return containers.flatMap(container -> {
            SearchIndexDocument.Builder docBuilder = statementFactory.document(searchSchema)
                    .entity(container);
            List<SearchFieldValue> containerSearchFields = docBuilder.build().getFieldValues();
            return Observable.fromIterable(containerSearchFields)
                    .filter(searchFieldValue -> !SKIPPED_FIELDS.contains(searchFieldValue.getField().getName())
                            && !searchFieldValue.getField().getName().startsWith(SYSTEM_FIELD_PREFIX))
                    .map(searchFieldValue -> toPair(searchFieldValue));            
        });
    }
    
    protected Pair<String, Object> toPair(SearchFieldValue searchFieldValue) {
        return new ImmutablePair<>(searchFieldValue.getField().getName(), searchFieldValue.getValue());
    }
}
