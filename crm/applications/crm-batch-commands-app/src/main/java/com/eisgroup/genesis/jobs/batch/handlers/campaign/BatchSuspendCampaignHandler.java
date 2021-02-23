/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
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

/**
 * @author Valeriy Sizonenko
 */
public class BatchSuspendCampaignHandler extends BaseBatchCampaignHandler {

    public static final String NAME = "batchSuspendCampaign";

    private static final String FILTER_STATE = "active";
    private static final String SUSPEND_FROM = "suspendFrom";
    private static final String SUSPEND_TO = "suspendTo";

    @Override
    protected Observable<SubsequentCommand> execute() {
        return searchEntities()
                .map(v -> {
                    Campaign campaign = ((Campaign) v);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty(RootEntityKey.ROOT_ID, v.getKey().getRootId().toString());
                    jsonObject.addProperty(SUSPEND_FROM, LocalDate.now().toString());
                    jsonObject.addProperty(SUSPEND_TO, campaign.getSuspendTo().toString());
                    return new SubsequentCommand(CrmCommands.SUSPEND_CAMPAIGN, () -> jsonObject, Variation.INVARIANT, v.getModelName());
                });
    }

    protected ModeledEntitySearchCriteria parseCriteria(Integer limit, Integer offset) {
        DomainModel model = modelRepo.getActiveModel(Campaign.NAME);
        String searchSchemaName = ModeledEntitySchemaResolver.getSearchSchemaNameUsing(model, Variation.INVARIANT);

        Collection<Matcher> searchMatchers = Arrays.asList(
                new SearchIndexQuery.FieldMatcher(SUSPEND_FROM, LocalDate.now().atStartOfDay()),
                new SearchIndexQuery.FieldMatcher(STATE, FILTER_STATE)
        );

        return new ModeledEntitySearchCriteria(searchSchemaName, searchMatchers, null,
                                               Arrays.asList(BaseKey.ROOT_ID, SUSPEND_TO), limit, offset);
    }

    @Override
    public String getName() {
        return NAME;
    }
}