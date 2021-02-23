/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.jobs.batch.handlers.campaign;

import com.eisgroup.genesis.criteria.Matcher;
import com.eisgroup.genesis.crm.commands.CrmCommands;
import com.eisgroup.genesis.facade.search.ModeledEntitySearchCriteria;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.modeling.types.Campaign;
import com.eisgroup.genesis.factory.repository.ModeledEntitySchemaResolver;
import com.eisgroup.genesis.jobs.lifecycle.api.commands.output.SubsequentCommand;
import com.eisgroup.genesis.json.key.BaseKey;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.model.Variation;
import com.eisgroup.genesis.search.SearchIndexQuery;
import com.google.gson.JsonObject;
import io.reactivex.Observable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Valeriy Sizonenko
 */
public class BatchStartCampaignHandler extends BaseBatchCampaignHandler {

    public static final String NAME = "batchStartCampaign";

    private static final String FILTER_STATE = "inactive";
    private static final String START_DATE = "startDate";
    private static final String AUTO_START = "autoStart";

    @Override
    protected Observable<SubsequentCommand> execute() {
        return searchEntities()
                .map(v -> {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty(RootEntityKey.ROOT_ID, v.getKey().getRootId().toString());
                    return new SubsequentCommand(CrmCommands.START_CAMPAIGN, () -> jsonObject, Variation.INVARIANT, v.getModelName());
                });
    }

    protected ModeledEntitySearchCriteria parseCriteria(Integer limit, Integer offset) {
        DomainModel model = modelRepo.getActiveModel(Campaign.NAME);
        String searchSchemaName = ModeledEntitySchemaResolver.getSearchSchemaNameUsing(model, Variation.INVARIANT);

        Collection<Matcher> searchMatchers = Arrays.asList(
                new SearchIndexQuery.FieldMatcher(START_DATE, LocalDate.now().atStartOfDay()),
                new SearchIndexQuery.FieldMatcher(AUTO_START, true),
                new SearchIndexQuery.FieldMatcher(STATE, FILTER_STATE)
        );

        return new ModeledEntitySearchCriteria(searchSchemaName, searchMatchers, null,
                Collections.singleton(BaseKey.ROOT_ID), limit, offset);
    }

    @Override
    public String getName() {
        return NAME;
    }
}