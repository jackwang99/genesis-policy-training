/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.personalauto.mapper;

import com.eisgroup.genesis.personalauto.rateable.RateablePersonalAutoBuilderTest;
import com.eisgroup.rating.input.impl.DefaultBusinessNode;
import com.eisgroup.rating.server.model.impl.DefaultMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class PersonalAutoTfsModelToRatingModelMapperTest {

    private static final String PERSONAL_AUTO_RATEABLE_MODEL =  "Node2.json";
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
        assertThat(policy.getPolicyPremiums(), arrayWithSize(4));
        assertThat(policy.getPolicyPremiums()[0].getCountryCode(), equalToIgnoringWhiteSpace("US"));
        assertThat(policy.getPolicyPremiums()[0].getStateCode(), equalToIgnoringWhiteSpace("NY"));


    }
}
