/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.campaign.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import com.eisgroup.genesis.crm.campaign.search.CampaignIndexPlugin;
import com.eisgroup.genesis.crm.campaign.search.CampaignSearchSchemaProviderPlugin;
import com.eisgroup.genesis.search.schema.SearchSchemaRegistry;

/**
 * @author Dmitry Andronchik
 */
@ConditionalOnClass(name = "com.eisgroup.genesis.search.plugin.ModeledSearchIndexPlugin")
public class CampaignSearchIntegrationConfig {

    private static final String CAMPAIGN_MODEL_NAME = "Campaign";

    @Bean
    public CampaignIndexPlugin campaignIndexPlugin() {
        return new CampaignIndexPlugin(CAMPAIGN_MODEL_NAME);
    }

    @Bean
    public CampaignSearchSchemaProviderPlugin campaignSearchSchemaProviderPlugin(SearchSchemaRegistry searchSchemaRegistry) {
        return new CampaignSearchSchemaProviderPlugin(CAMPAIGN_MODEL_NAME, searchSchemaRegistry);
    }
}
