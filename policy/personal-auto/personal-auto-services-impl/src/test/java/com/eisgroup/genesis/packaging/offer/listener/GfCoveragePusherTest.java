/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work
 * under U.S. copyright laws. CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may
 * be copied, distributed, modified, or incorporated into any other media without EIS Group prior
 * written consent.
 */
package com.eisgroup.genesis.packaging.offer.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.eisgroup.genesis.factory.model.personalauto.AutoBLOB;
import com.eisgroup.genesis.factory.model.personalauto.AutoLOB;
import com.eisgroup.genesis.factory.model.personalauto.AutoVehicle;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.factory.modeling.types.PremiumHolderEntity;
import com.eisgroup.genesis.factory.utils.selector.JsonNode;
import static org.junit.Assert.assertEquals;

/**
 * Test for {@link GfCoveragePusher}
 * @author aspichakou
 * @since 1.0
 */
public class GfCoveragePusherTest extends AbstractGfTest{
    
    @Mock
    GfCoverageCollector collector;  
    
    @InjectMocks
    GfCoveragePusher testObject;
    
    @Before
    public void setUp() {
        super.setUp();
    }
    
    @Test
    public void testTraverser() {
        PolicySummary policySummary = setupPolicy();

        // Collect
        policySummary = removeGFCoverages(policySummary);        
         
        final List<JsonNode> gfEntities = collector.getGfEntities(policySummary.toJson(), policySummary.getTransactionDetails().getTxEffectiveDate().toLocalDate());
        
        testObject.pushGfEntitites(policySummary.toJson(), gfEntities);
        
        // Checking        
        final List<JsonNode> gfEntities2 = collector.getGfEntities(policySummary.toJson(), policySummary.getTransactionDetails().getTxEffectiveDate().toLocalDate());
        assertEquals(gfEntities2.size(), gfEntities.size());        
    }

	private PolicySummary removeGFCoverages(PolicySummary policySummary) {
		final AutoBLOB autoBLOB = (AutoBLOB)policySummary.getBlob();
		final AutoLOB lob = (AutoLOB)autoBLOB.getLobs().iterator().next();
        final ArrayList<AutoVehicle> riskItems = new ArrayList(lob.getRiskItems());
        final AutoVehicle vehicle = riskItems.get(0);
        // Remove all GF coverages
        final List<PremiumHolderEntity> filtered = vehicle.getCoverages().stream().filter(c->{            
            c.toJson().remove("coverage");
            return !(GF_COVERAGE.equals(c.getCode()));
        }).collect(Collectors.toList());
        vehicle.setCoverages(filtered);        
        riskItems.get(1).toJson().remove("coverages");
        
        lob.setRiskItems((Collection)riskItems);
        autoBLOB.setLobs(Arrays.asList(lob));
        policySummary.setBlob(autoBLOB);
        return policySummary;
	}
}
