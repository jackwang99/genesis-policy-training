/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.facade;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.eisgroup.genesis.facade.endpoint.command.CommandEndpoint;
import com.eisgroup.genesis.facade.endpoint.load.LoadCurrentEntityVersionEndpoint;
import com.eisgroup.genesis.facade.endpoint.load.LoadEntityEndpoint;
import com.eisgroup.genesis.facade.endpoint.load.LoadHistoryEndpoint;
import com.eisgroup.genesis.facade.endpoint.model.ModelFacade;
import com.eisgroup.genesis.facade.endpoint.search.ExtendedCampaignSearchEndpoint;
import com.eisgroup.genesis.facade.endpoint.search.FullTextSearchEndpoint;
import com.eisgroup.genesis.facade.module.EndpointPackage;
import com.eisgroup.genesis.facade.module.FacadeModule;
import com.eisgroup.genesis.kraken.facade.KrakenBulkModelEndpoint;
import com.eisgroup.genesis.kraken.facade.KrakenBundleRepositoryEndpoint;

/**
 * Campaign facade metainformation
 * 
 * @author Valeriy Sizonenko
 *
 */
public class CampaignFacade implements FacadeModule {

    @Override
    public String getModelType() {
        return "Campaign";
    }

    @Override
    public String getModelName() {
        return "Campaign";
    }

    @Override
    public int getFacadeVersion() {
        return 1;
    }

    @Override
    public Collection<EndpointPackage> getEndpoints() {
        Set<EndpointPackage> endpoints = new HashSet<>();
        endpoints.add(new ModelFacade());
        endpoints.add(new CommandEndpoint());
        endpoints.add(new LoadEntityEndpoint());
        endpoints.add(new LoadHistoryEndpoint());
        endpoints.add(new LoadCurrentEntityVersionEndpoint());
        endpoints.add(new ChildCampaignEndpoint());
        endpoints.add(new FullTextSearchEndpoint());
        endpoints.add(new ExtendedCampaignSearchEndpoint());
        endpoints.add(new PreviewTargetListForCampaignEndpoint());
        endpoints.add(new KrakenBundleRepositoryEndpoint());
        endpoints.add(new KrakenBulkModelEndpoint());
        return endpoints;
    }

}
