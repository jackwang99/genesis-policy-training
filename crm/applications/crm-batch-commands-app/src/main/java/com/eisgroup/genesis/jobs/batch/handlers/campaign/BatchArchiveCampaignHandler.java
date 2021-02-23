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
import com.eisgroup.genesis.versioning.Version;
import com.google.gson.JsonObject;
import io.reactivex.Observable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Valeriy Sizonenko
 * @since 9.14
 */
public class BatchArchiveCampaignHandler extends BaseBatchCampaignHandler {

    public static final String NAME = "batchArchiveCampaign";

    private static final String TERMINATED_STATE = "terminated";
    private static final String COMPLETED_STATE = "completed";
    private static final long DAYS = 365;

    protected Observable<SubsequentCommand> execute() {
        return searchEntities()
                .map(v -> {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty(RootEntityKey.ROOT_ID, v.getKey().getRootId().toString());
                    return new SubsequentCommand(CrmCommands.ARCHIVE_CAMPAIGN, () -> jsonObject, Variation.INVARIANT, v.getModelName());
                });
    }

    protected ModeledEntitySearchCriteria parseCriteria(Integer limit, Integer offset) {
        DomainModel model = modelRepo.getActiveModel(Campaign.NAME);
        String searchSchemaName = ModeledEntitySchemaResolver.getSearchSchemaNameUsing(model, Variation.INVARIANT);

        Collection<Matcher> searchMatchers = Arrays.asList(
                new SearchIndexQuery.FieldMatcher(Version.ATTR_UPDATED_ON, SearchIndexQuery.FieldMatcher.Operand.lte, Collections.singletonList(LocalDate.now().atStartOfDay().minusDays(DAYS))),
                new SearchIndexQuery.OrMatcher(new Matcher[]{new SearchIndexQuery.FieldMatcher(STATE, TERMINATED_STATE), new SearchIndexQuery.FieldMatcher(STATE, COMPLETED_STATE)})
        );

        return new ModeledEntitySearchCriteria(searchSchemaName, searchMatchers, null,
                                               Collections.singleton(BaseKey.ROOT_ID), limit, offset);
    }

    @Override
    public String getName() {
        return NAME;
    }
}