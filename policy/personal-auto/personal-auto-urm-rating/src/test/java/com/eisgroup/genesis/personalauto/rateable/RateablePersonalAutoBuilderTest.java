/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.personalauto.rateable;

import com.eisgroup.genesis.exception.InvocationError;
import com.eisgroup.genesis.policy.core.rating.services.rateable.DefaultJsonReducer;
import com.eisgroup.rating.input.BusinessNode;
import com.eisgroup.rating.input.Node;
import com.eisgroup.rating.input.impl.DefaultBusinessNode;
import com.eisgroup.rating.input.impl.NodeList;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author dl on 7/31/17.
 */
public class RateablePersonalAutoBuilderTest {

    private static final String NAME = "name";

    private static final String QUOTE_PATH1 = "personal-auto-test.json";

    @Test
    public void testRateableModelForPersonalAuto() {
        String file = readFile(QUOTE_PATH1);
        JsonObject quote = (JsonObject) new JsonParser().parse(file);

        Node node = new DefaultJsonReducer().build(() -> quote, "PersonalAuto");

        assertThat(node.getType(), is("PersonalAutoPolicySummary"));

        assertTrue(node instanceof DefaultBusinessNode);

        BusinessNode policyNode = (BusinessNode)node;
        assertThat(policyNode.getChildren(), hasSize(4));

        assertNotNull(policyNode.getChildrenByName().get("termDetails"));
        
        Node blob = policyNode.getChildrenByName().get("blob");
        assertNotNull(blob);
        
        final Node lobsNode = ((BusinessNode)blob).getChildrenByName().get("lobs");
        assertTrue(lobsNode.isCollection());
        
        final NodeList lobs = (NodeList)lobsNode;
        assertNotNull(lobs);
                
        final BusinessNode lob = (BusinessNode)lobs.getElements().get(0);
        assertNotNull(lob.getChildrenByName().get("riskItems"));

        assertThat(policyNode.getChildren(), contains(
                hasProperty(NAME, is("termDetails")),               
                hasProperty(NAME, is("transactionDetails")),
                hasProperty(NAME, is("blob")),
                hasProperty(NAME, is("id"))
        ));
        assertThat(lob.getChildren(), contains(
                hasProperty(NAME, is("id")),
                hasProperty(NAME, is("riskItems"))                
        ));

    }

    public static String readFile(String path) {
        try {
            return IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            throw new InvocationError("check path configuration");
        }
    }

}
