/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.campaign.search;

import java.math.BigDecimal;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.eisgroup.genesis.factory.modeling.types.StringTargetCharacteristic;
import com.eisgroup.genesis.factory.modeling.types.TargetCharacteristic;
import com.eisgroup.genesis.factory.modeling.types.Campaign;
import com.eisgroup.genesis.factory.modeling.types.DateTargetCharacteristic;
import com.eisgroup.genesis.json.key.BaseKey;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import com.eisgroup.genesis.util.DateUtil;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.reactivex.Observable;

import com.eisgroup.genesis.crm.campaign.config.CampaignSearchIntegrationConfig;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.json.IdentifiableEntity;
import com.eisgroup.genesis.factory.model.domain.DomainModel;

/**
 * 
 * @author Dmitry Andronchik
 */
public class CampaignIndexPluginTest {

    private static final String CAMPAIGN_MODEL_NAME = "Campaign";
    
    private ModelRepository<DomainModel> modelRepository = ModelRepositoryFactory.getRepositoryFor(DomainModel.class);
    
    private CampaignIndexPlugin plugin;
    
    @Before
    public void setUp() {
        plugin = new CampaignSearchIntegrationConfig().campaignIndexPlugin();
    }    
    
    @Test
    public void testResolveStringCriteriaValue() {
        TargetCharacteristic tc = initTargetCharacteristic("GenesisStringTargetCharacteristic");
        JsonElement jsonTcValue = parseJsonElement("tcValue");
        
        Assert.assertEquals("tcValue", plugin.resolveCriteriaValue(jsonTcValue, tc));
    }
    
    @Test
    public void testResolveDecimalCriteriaValue() {
        TargetCharacteristic tc = initTargetCharacteristic("GenesisDecimalTargetCharacteristic");
        JsonElement jsonTcValue = parseJsonElement("100.05");
        
        Assert.assertEquals(BigDecimal.valueOf(100.05), plugin.resolveCriteriaValue(jsonTcValue, tc));
    }
    
    @Test
    public void testResolveDateCriteriaValue() {
        TargetCharacteristic tc = initTargetCharacteristic("GenesisDateTargetCharacteristic");
        JsonElement jsonTcValue = parseJsonElement("2017-05-12");
        
        Assert.assertEquals(DateUtil.toDateTime("2017-05-12"), plugin.resolveCriteriaValue(jsonTcValue, tc));
    }
    
    @Test
    public void testResolveAdditionalFieldsWrongModel() {
        
        DomainModel wrongModel = new DomainModel(null, "WROMG_MODEL_NAME", "-1", null, null, null, null, null, null);
        
        Observable<Pair<String, Object>> additionalFields = plugin.resolveAdditionalFields(wrongModel, new IdentifiableEntity() {
            @Override
            public JsonObject toJson() {
                return null;
            }
            
            @Override
            public BaseKey getKey() {
                return null;
            }
        });
        Assert.assertEquals(Long.valueOf(0), additionalFields.count().blockingGet());
    }
    
    @Test
    public void testResolveAdditionalFieldsForCampaignWithoutTargetCharacteristics() {
        
        DomainModel model = modelRepository.getActiveModel(CAMPAIGN_MODEL_NAME);
        Campaign campaign = (Campaign) ModelInstanceFactory.createRootInstance(CAMPAIGN_MODEL_NAME, "1");
        
        Observable<Pair<String, Object>> additionalFields = plugin.resolveAdditionalFields(model, campaign);
        Assert.assertFalse(additionalFields.blockingIterable().iterator().hasNext());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testResolveAdditionalFields() {
        
        DomainModel model = modelRepository.getActiveModel(CAMPAIGN_MODEL_NAME);
        Campaign campaign = initCampaignWithTargetCharacteristics();
        
        Observable<Pair<String, Object>> additionalFields = plugin.resolveAdditionalFields(model, campaign);
        
        Assert.assertThat(additionalFields.toList().blockingGet(), Matchers.containsInAnyOrder(new ImmutablePair<>("tc_firstName_matches", Lists.newArrayList("Winnie", "Mickey")),
                new ImmutablePair<>("tc_birthDate_from", DateUtil.toDateTime("2000-01-15")), new ImmutablePair<>("tc_birthDate_to", DateUtil.toDateTime("2000-01-19"))));
    }    
    
    private Campaign initCampaignWithTargetCharacteristics() {
        Campaign campaign = (Campaign) ModelInstanceFactory.createRootInstance(CAMPAIGN_MODEL_NAME, "1");
        
        StringTargetCharacteristic firstNameTc = (StringTargetCharacteristic) initTargetCharacteristic("GenesisStringTargetCharacteristic", "firstName");
        firstNameTc.setMatches(Lists.newArrayList("Winnie", "Mickey"));
        
        DateTargetCharacteristic birthDateTc = (DateTargetCharacteristic) initTargetCharacteristic("GenesisDateTargetCharacteristic", "birthDate");
        birthDateTc.setFrom(DateUtil.toDate("2000-01-15"));
        birthDateTc.setTo(DateUtil.toDate("2000-01-19"));
        
        campaign.setTargetCharacteristics(Lists.newArrayList(firstNameTc, birthDateTc));
        
        return campaign;
    }
    
    private TargetCharacteristic initTargetCharacteristic(String type) {
        return initTargetCharacteristic(type, null);
    }
    
    private TargetCharacteristic initTargetCharacteristic(String type, String tcName) {
        TargetCharacteristic tc = (TargetCharacteristic) ModelInstanceFactory.createInstance(CAMPAIGN_MODEL_NAME, "1", type);
        tc.setName(tcName);        
        return tc;
    }
    
    private JsonElement parseJsonElement(String jsonString) {
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(jsonString);
    }
}
