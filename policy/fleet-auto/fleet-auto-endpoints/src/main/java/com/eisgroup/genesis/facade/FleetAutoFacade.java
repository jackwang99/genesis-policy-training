/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.facade;

import com.eisgroup.genesis.decision.facade.DecisionTestEndpoint;
import com.eisgroup.genesis.facade.module.EndpointPackage;
import com.eisgroup.genesis.policy.core.facade.integration.PolicyLoadIntegrationEndpoint;

import com.eisgroup.genesis.policy.pnc.auto.facade.AbstractAutoPolicyFacade;

import java.util.Collection;

/**
 * Personal Auto Product Specific Facade Configuration.
 * 
 * @author azukovskij
 * @since 1.0
 */
public class FleetAutoFacade extends AbstractAutoPolicyFacade {
    
	@Override
    public String getModelName() {
        return "FleetAuto";
    }

    @Override
    public int getFacadeVersion() {
        return 1;
    }

    @Override
    public Collection<EndpointPackage> getEndpoints() {
        Collection<EndpointPackage> endpoints = super.getEndpoints();
        endpoints.add(new DecisionTestEndpoint());
        endpoints.add(new PolicyLoadIntegrationEndpoint());
        return endpoints;
    }
}
