/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.container.search;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.eisgroup.genesis.crm.core.service.AgencyContainerService;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.search.SearchIndexStatementFactory;

/**
 * 
 * @author Dmitry Andronchik
 * @since 10.3
 */
public class AgencyContainerIndexPluginTest {

    private static final String customerModelName = "INDIVIDUALCUSTOMER";
    private static final String containerModelName = "IndividualAgencyContainer";
    
    @Mock
    private SearchIndexStatementFactory statementFactory;
    @Mock
    private AgencyContainerService agencyContainerService;
    @Mock
    private ModelResolver modelResolver;    
    
    private AgencyContainerIndexPlugin plugin;
    
    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        
        plugin = new AgencyContainerIndexPlugin(customerModelName, containerModelName, statementFactory, agencyContainerService); 
    }
    
    @Test
    public void resolveAdditionalFieldsWrongModel() {
        
        DomainModel wrongModel = new DomainModel("Campaign", "Campaign", "1", null, null, null, null, null, null, null);
        
        plugin.resolveAdditionalFields(wrongModel, null)
                .test()
                .assertComplete()
                .assertNoValues();
    }
}

