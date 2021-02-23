/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.campaign.search;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.eisgroup.genesis.factory.json.IdentifiableEntity;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.modeling.types.immutable.Campaign;
import com.eisgroup.genesis.factory.modeling.types.immutable.TargetCharacteristic;
import com.eisgroup.genesis.factory.modeling.types.immutable.StringTargetCharacteristic;
import com.eisgroup.genesis.factory.modeling.types.immutable.DateTargetCharacteristic;
import com.eisgroup.genesis.factory.modeling.types.immutable.DecimalTargetCharacteristic;
import com.eisgroup.genesis.search.plugin.ModeledSearchIndexPlugin;
import com.eisgroup.genesis.util.DateUtil;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.reactivex.Observable;

/**
 * 
 * @author Dmitry Andronchik
 */
public class CampaignIndexPlugin implements ModeledSearchIndexPlugin {
    
    public static final String TARGET_CHARACTERISTIC_FIELD_FROM = "tc_%s_from";
    public static final String TARGET_CHARACTERISTIC_FIELD_TO = "tc_%s_to";
    public static final String TARGET_CHARACTERISTIC_FIELD_MATCHES = "tc_%s_matches";
    public static final String FROM_FIELD = "from";
    public static final String TO_FIELD = "to";
    public static final String MATCHES_FIELD = "matches";    

    private final String campaignModelName;
    
    public CampaignIndexPlugin(String campaignModelName) {
            this.campaignModelName = campaignModelName;
    }    
    
    @Override
    public Observable<Pair<String, Object>> resolveAdditionalFields(DomainModel model, IdentifiableEntity entity) {
        if(!StringUtils.equals(model.getName(), campaignModelName) || !(entity instanceof Campaign)) {
            return Observable.empty();
        }        
        Campaign campaign = (Campaign) entity;
        if ((campaign.getTargetCharacteristics() == null) || campaign.getTargetCharacteristics().isEmpty()) {
            return Observable.empty();
        }
        return Observable.<TargetCharacteristic>fromIterable(campaign.getTargetCharacteristics())
                        .flatMap(tc -> toPairs(tc));
    }
    
    protected Observable<Pair<String, Object>> toPairs(TargetCharacteristic tc) {
        List<Pair<String, Object>> indexPairs = Lists.newArrayList();

        JsonObject jsonTc = tc.toJson();
        if(jsonTc.has(FROM_FIELD) && !jsonTc.get(FROM_FIELD).isJsonNull()) {
            indexPairs.add(new ImmutablePair<>(String.format(TARGET_CHARACTERISTIC_FIELD_FROM, tc.getName()), resolveCriteriaValue(jsonTc.get(FROM_FIELD), tc)));
        }
        if(jsonTc.has(TO_FIELD) && !jsonTc.get(TO_FIELD).isJsonNull()) {
            indexPairs.add(new ImmutablePair<>(String.format(TARGET_CHARACTERISTIC_FIELD_TO, tc.getName()), resolveCriteriaValue(jsonTc.get(TO_FIELD), tc)));                
        }
        if(jsonTc.has(MATCHES_FIELD) && jsonTc.get(MATCHES_FIELD).isJsonArray() && (jsonTc.getAsJsonArray(MATCHES_FIELD).size() > 0)) {
            JsonArray jsonArrayValue = jsonTc.getAsJsonArray(MATCHES_FIELD);
            List<Object> matchesValues = Lists.newArrayList();
            jsonArrayValue.forEach(jsonElem -> matchesValues.add(resolveCriteriaValue(jsonElem, tc)));
            indexPairs.add(new ImmutablePair<>(String.format(TARGET_CHARACTERISTIC_FIELD_MATCHES, tc.getName()), matchesValues));                
        }            
        return Observable.fromIterable(indexPairs);        
    }
    
    protected Object resolveCriteriaValue(JsonElement jsonTc, TargetCharacteristic tc) {
        if (tc instanceof StringTargetCharacteristic) {
            return jsonTc.getAsString();
        } else if (tc instanceof DateTargetCharacteristic) {
            return DateUtil.toDateTime(jsonTc.getAsString());            
        } else if (tc instanceof DecimalTargetCharacteristic) {
            return jsonTc.getAsBigDecimal();            
        } else {
            throw new UnsupportedOperationException("Unsupported target characteristic " + tc.getClass());
        }
    }
}
