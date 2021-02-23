/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.facade;

import com.eisgroup.genesis.facade.endpoint.dimension.AgencyContainerFetchEndpoint;
import com.eisgroup.genesis.facade.endpoint.search.CustomerFullTextSearchEndpoint;
import com.eisgroup.genesis.facade.module.EndpointPackage;

import java.util.Collection;

/**
 * Individual customer gateway
 * 
 * @author azukovskij, avoitau
 *
 */
public class INDIVIDUALCUSTOMERFacade extends BaseCrmFacade {


    @Override
    public String getModelName() {
        return "INDIVIDUALCUSTOMER";
    }

    @Override
    public int getFacadeVersion() {
        return 1;
    }
    
    @Override
    public Collection<EndpointPackage> getEndpoints() {
        Collection<EndpointPackage> endpoints = super.getEndpoints();
        endpoints.add(new CustomerFullTextSearchEndpoint());
        endpoints.add(new AgencyContainerFetchEndpoint());
        endpoints.add(new LoadCustomerWithContainers());
        endpoints.add(new IndividualCustomerLeadImportEndpoint());
        return endpoints;
    }

}
