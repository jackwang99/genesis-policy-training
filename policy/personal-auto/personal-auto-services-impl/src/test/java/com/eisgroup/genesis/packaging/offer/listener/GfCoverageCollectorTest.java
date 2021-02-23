/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work
 * under U.S. copyright laws. CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may
 * be copied, distributed, modified, or incorporated into any other media without EIS Group prior
 * written consent.
 */
package com.eisgroup.genesis.packaging.offer.listener;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.factory.utils.selector.JsonNode;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Test for {@link GfCoverageCollector}
 * 
 * @author aspichakou
 * @since 1.0
 */
public class GfCoverageCollectorTest extends AbstractGfTest {
    
    @InjectMocks
    private GfCoverageCollector testObject = new GfCoverageCollector();

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testCollector() {
        final PolicySummary policySummary = setupPolicy();

        final List<JsonNode> gfEntities = testObject.getGfEntities(policySummary.toJson(), policySummary.getTransactionDetails().getTxEffectiveDate().toLocalDate());
        assertNotNull(gfEntities);
        assertEquals(3, gfEntities.size());
    }

}
