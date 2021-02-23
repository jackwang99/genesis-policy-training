/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.personalhome.rateable;

import com.eisgroup.genesis.exception.InvocationError;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author zhachen
 */
public class RateablePersonalHomeBuilderTest {

    private static final String NAME = "name";

    private static final String QUOTE_PATH1 = "personal-home-test.json";

    public static String readFile(String path) {
        try {
            return IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            throw new InvocationError("check path configuration", e);
        }
    }

}
