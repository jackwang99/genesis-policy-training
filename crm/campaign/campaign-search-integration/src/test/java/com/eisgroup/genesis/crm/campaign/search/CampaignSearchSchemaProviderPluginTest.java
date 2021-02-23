/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.campaign.search;

import java.util.Collection;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.eisgroup.genesis.crm.campaign.config.CampaignSearchIntegrationConfig;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import com.eisgroup.genesis.search.schema.SearchField;
import com.eisgroup.genesis.search.schema.SearchSchemaRegistry;


/**
 * 
 * @author Dmitry Adronchik
 */
public class CampaignSearchSchemaProviderPluginTest {
    
    private static final String CAMPAIGN_MODEL_NAME = "Campaign";
    
    private ModelRepository<DomainModel> modelRepository = ModelRepositoryFactory.getRepositoryFor(DomainModel.class);    

    private CampaignSearchSchemaProviderPlugin plugin;
    
    @Before
    public void setUp() {
        plugin = new CampaignSearchIntegrationConfig().campaignSearchSchemaProviderPlugin(new SearchSchemaRegistry());
    }
    
    @Test
    public void testResolveAdditionalFieldsWrongModel() {
     
        DomainModel wrongModel = new DomainModel(null, "WROMG_MODEL_NAME", "-1", null, null, null, null, null, null);
        
        Collection<SearchField> searchFields = plugin.resolveAdditionalFields(wrongModel);
        Assert.assertEquals(0, searchFields.size());        
    }
    
    @Test
    public void testResolveAdditionalFields() {
     
        DomainModel model = modelRepository.getActiveModel(CAMPAIGN_MODEL_NAME);
        
        Collection<SearchField> searchFields = plugin.resolveAdditionalFields(model);
        Assert.assertFalse(searchFields.isEmpty());
        
        Assert.assertThat(searchFields.stream()
                .map(searchField -> searchField.getName())
                .collect(Collectors.toList()), Matchers.hasItems("tc_firstName_from", "tc_lastName_to", "tc_customerNumber_matches"));        
    }
}
