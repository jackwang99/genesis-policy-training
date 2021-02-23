/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work
 * under U.S. copyright laws. CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may
 * be copied, distributed, modified, or incorporated into any other media without EIS Group prior
 * written consent.
 */
package com.eisgroup.genesis.packaging.offer.listener;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.packaging.UserOperation;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link GrandfatherOfferManagerListener}
 * 
 * @author aspichakou
 * @since 1.0
 */
public class GrandfatherOfferManagerListenerTest extends AbstractGfTest {

    protected static final String GF_COVERAGE = "GFCoverage";

    @Mock
    protected ModelResolver modelResolver;

    @Mock
    private GfCoverageCollector coverageCollector;

    @Mock
    private GfCoveragePusher coveragePusher;

    @InjectMocks
    private GrandfatherOfferManagerListener testObject;

    @Test
    public void shouldSelect() {
        final PolicySummary policy = setupPolicy();
        final PolicySummary policyCopy = setupPolicy();
        testObject.execute(policy, policyCopy, "grandfather", mock(UserOperation.class)).test();

        verify(coverageCollector, times(1)).getGfEntities(any(), any());
        verify(coveragePusher, times(1)).pushGfEntitites(any(), any());
    }
}
