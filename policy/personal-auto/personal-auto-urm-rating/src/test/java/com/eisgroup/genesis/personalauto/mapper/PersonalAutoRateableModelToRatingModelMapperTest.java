/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.personalauto.mapper;

import com.eisgroup.genesis.personalauto.rateable.RateablePersonalAutoBuilderTest;
import com.eisgroup.rating.input.impl.DefaultBusinessNode;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.eisgroup.rating.server.model.impl.DefaultMapper;

import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test mapping from Personal Auto Rateable model to the test policy model
 *
 * @author Denis Levchuk on 8/2/17.
 */
public class PersonalAutoRateableModelToRatingModelMapperTest {

    private static final String PERSONAL_AUTO_RATEABLE_MODEL = "personal-auto-rateable.json";
    private static final String TEST_POLICY_CLASS = "com.eisgroup.genesis.personalauto.mapper.TestPolicy";

    @Test
    public void testMapping() throws IOException {
        String json = RateablePersonalAutoBuilderTest.readFile(PERSONAL_AUTO_RATEABLE_MODEL);

        ObjectMapper jsonMapper = new ObjectMapper();
        DefaultBusinessNode businessNode = jsonMapper.readValue(json, DefaultBusinessNode.class);

        DefaultMapper mapper = new DefaultMapper();
        mapper.setDestinationClassResolver(s -> {
            try {
                return Thread.currentThread().getContextClassLoader().loadClass(s);
            } catch (ClassNotFoundException e) {
                return null;
            }
        });

        TestPolicy policy = (TestPolicy) mapper.map(businessNode, TEST_POLICY_CLASS);
        assertThat(policy, notNullValue());

        assertThat(policy.getTermDetails(), notNullValue());
        assertThat(policy.getTermDetails().getTermEffectiveDate(), notNullValue());

        assertThat(policy.getRiskItems(), notNullValue());
        assertThat(policy.getRiskItems().length, is(1));

        TestPolicy.Vehicle vehicle = policy.getRiskItems()[0];

        assertThat(vehicle.getCollSymbol(), is("1"));
        assertThat(vehicle.getAntiLockBrakeCd(), is("ABR"));
        assertThat(vehicle.getLiabSymbol(), nullValue());
        assertThat(vehicle.getBiSymbol(), is("1"));
        assertThat(vehicle.getModel(), is("X5"));
        assertThat(vehicle.getUsageCd(), is("PL"));
        assertThat(vehicle.getAnnualMiles(), is(5000L));
        assertThat(vehicle.getRecoveryDeviceInd(), nullValue());
        assertThat(vehicle.getArmoredInd(), is(false));
        assertThat(vehicle.getCompSymbol(), is("1"));
        assertThat(vehicle.getModelYear(), is(2012L));
        assertThat(vehicle.getPipMedSymbol(), is("1"));
        assertThat(vehicle.getPipMedSymbol(), is("1"));
        assertThat(vehicle.getSecurityOptionsCd(), is("TRCKSYS"));
        assertThat(vehicle.getDaytimeRunningLampsInd(), is(false));
        assertThat(vehicle.getAdjustedValue(), nullValue());
        assertThat(vehicle.getAirBagStatusCd(), is("DAP"));
        assertThat(vehicle.getPdSymbol(), is("1"));
        assertThat(vehicle.getTypeCd(), is("Regular"));
        assertThat(vehicle.getAutomaticBeltsInd(), is(false));
        assertThat(vehicle.getAddress(), nullValue());
        assertThat(vehicle.getVariations().length, is(1));
        assertThat(vehicle.getVariations()[0].getCoverages().length, is(1));
        assertThat(vehicle.getVariations()[0].getCoverages()[0].getLimitAmount(), is(BigDecimal.valueOf(250)));
    }
}
