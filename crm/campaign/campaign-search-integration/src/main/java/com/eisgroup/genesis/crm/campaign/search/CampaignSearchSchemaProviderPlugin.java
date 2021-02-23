/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.campaign.search;

import com.eisgroup.genesis.crm.core.service.search.BaseModeledEntitySearchSchemaProviderPlugin;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.search.schema.JsonPathBuilder;
import com.eisgroup.genesis.search.schema.SearchField;
import com.eisgroup.genesis.search.schema.SearchSchemaRegistry;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Creates additional fields for {@Campaign} from targetModel.
 * 
 * @author Dmitry Andronchik
 */
public class CampaignSearchSchemaProviderPlugin extends BaseModeledEntitySearchSchemaProviderPlugin {
    
    private static final String INDIVIDUAL_CUSTOMER_MODEL = "INDIVIDUALCUSTOMER";
    private static final String ORGANIZATION_CUSTOMER_MODEL = "ORGANIZATIONCUSTOMER";    

    private static final String TARGET_CHARACTERISTIC_FIELD_FROM = "tc_%s_from";
    private static final String TARGET_CHARACTERISTIC_FIELD_TO = "tc_%s_to";
    private static final String TARGET_CHARACTERISTIC_FIELD_MATCHES = "tc_%s_matches";

    protected final SearchSchemaRegistry searchSchemaRegistry;    

    private final String campaignModelName;

    public CampaignSearchSchemaProviderPlugin(String campaignModelName, SearchSchemaRegistry searchSchemaRegistry) {
        this.campaignModelName = campaignModelName;
        this.searchSchemaRegistry = searchSchemaRegistry;
    }

    @Override
    public Collection<SearchField> resolveAdditionalFields(DomainModel model) {
        if (!StringUtils.equals(model.getName(), campaignModelName)) {
            return Sets.newHashSet();
        }

        Set<SearchField> searchableModelFields = Sets.newHashSet();
        searchableModelFields.addAll(getSearchableModelFields(INDIVIDUAL_CUSTOMER_MODEL));
        searchableModelFields.addAll(getSearchableModelFields(ORGANIZATION_CUSTOMER_MODEL));        
       
        return searchableModelFields
                .stream()
                .flatMap(customerSearchField -> toSearchFields(customerSearchField)).collect(Collectors.toList());
    }

    private Stream<SearchField> toSearchFields(SearchField customerSearchField) {

        return Lists.newArrayList(toSearchField(customerSearchField, TARGET_CHARACTERISTIC_FIELD_FROM),
                toSearchField(customerSearchField, TARGET_CHARACTERISTIC_FIELD_TO),
                toSearchField(customerSearchField, TARGET_CHARACTERISTIC_FIELD_MATCHES)).stream();
    }

    private SearchField toSearchField(SearchField customerSearchField, String tcPattern) {
        String tcFieldName = String.format(tcPattern, customerSearchField.getName());
        return new SearchField(tcFieldName, customerSearchField.getType(), true,
                new JsonPathBuilder().attr(tcFieldName, true).build());
    }
}
