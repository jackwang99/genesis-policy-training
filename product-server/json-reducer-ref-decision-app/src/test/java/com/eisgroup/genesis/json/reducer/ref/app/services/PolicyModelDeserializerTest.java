package com.eisgroup.genesis.json.reducer.ref.app.services;/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

import static com.eisgroup.genesis.json.reducer.ref.app.services.JsonReducerRunner.PERSON_AUTO_SPECIFICATION_JSON;
import static com.eisgroup.genesis.json.reducer.ref.app.services.JsonReducerRunner.readFile;
import static com.eisgroup.genesis.json.reducer.ref.app.services.JsonReducerRunner.PolicySpecificationDeserializer.fromJson;
import static com.eisgroup.genesis.json.reducer.ref.app.services.JsonReducerRunner.PolicySpecificationDeserializer.resolvePolicySpecifications;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import com.eisgroup.genesis.product.specification.policy.PolicyModel;
import org.junit.Test;

import com.eisgroup.genesis.product.specification.general.Component;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

/**
 * Test {@link PolicyModel} deserializer
 *
 * @author ssauchuk
 * @since 10.2
 */
public class PolicyModelDeserializerTest {

    @Test
    public void shouldParsePolicySpecificationFromJson() {

        JsonArray policySpecifications = resolvePolicySpecifications(
                new JsonParser().parse(readFile(PERSON_AUTO_SPECIFICATION_JSON)));

        assertThat(policySpecifications.size(), is(1));

        PolicyModel filteredSpecification = fromJson(policySpecifications.get(0));

        assertNotNull(filteredSpecification);

        assertThat(filteredSpecification.getComponents().size(), is(2));

        assertThat(filteredSpecification.getComponents().stream()
                .map(Component::getAttributes)
                .flatMap(Collection::stream).count(), is(4L));
    }
}
