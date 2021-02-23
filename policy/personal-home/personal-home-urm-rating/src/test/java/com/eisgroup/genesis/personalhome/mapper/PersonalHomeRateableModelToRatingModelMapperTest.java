/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.personalhome.mapper;

import com.eisgroup.genesis.personalhome.rateable.RateablePersonalHomeBuilderTest;
import com.eisgroup.rating.input.impl.DefaultBusinessNode;
import com.eisgroup.rating.server.model.impl.DefaultMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test mapping from Personal Home Rateable model to the test policy model
 *
 * @author zhchen
 * @since 1.0
 */
public class PersonalHomeRateableModelToRatingModelMapperTest {

    private static final String PERSONAL_HOME_RATEABLE_MODEL = "personal-home-rateable.json";
    private static final String TEST_POLICY_CLASS = "com.eisgroup.genesis.personalhome.mapper.TestPolicy";
    private static final Logger logger = LoggerFactory.getLogger(PersonalHomeRateableModelToRatingModelMapperTest.class);

    @Test
    public void testMapping() throws IOException {
        String json = RateablePersonalHomeBuilderTest.readFile(PERSONAL_HOME_RATEABLE_MODEL);

        ObjectMapper jsonMapper = new ObjectMapper();
        DefaultBusinessNode businessNode = jsonMapper.readValue(json, DefaultBusinessNode.class);

        DefaultMapper mapper = new DefaultMapper();
        mapper.setDestinationClassResolver(s -> {
            try {
                return Thread.currentThread().getContextClassLoader().loadClass(s);
            } catch (ClassNotFoundException e) {
                logger.error("Class not found", e);
                return null;
            }
        });

        TestPolicy policy = (TestPolicy) mapper.map(businessNode, TEST_POLICY_CLASS);
        assertThat(policy, notNullValue());

        assertThat(policy.getTermDetails(), notNullValue());
        assertThat(policy.getTermDetails().getTermEffectiveDate(), notNullValue());

        assertThat(policy.getRiskItems(), notNullValue());
        assertThat(policy.getRiskItems().length, is(1));

        TestPolicy.RiskItemslocation riskItem = policy.getRiskItems()[0];

        assertThat(riskItem.getAddress(), nullValue());
    }
}
