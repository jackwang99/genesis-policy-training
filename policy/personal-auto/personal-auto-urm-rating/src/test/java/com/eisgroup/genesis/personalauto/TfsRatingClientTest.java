/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.personalauto;

import com.eisgroup.genesis.exception.InvocationError;
import com.eisgroup.genesis.tfs.services.api.impl.DefaultTfsCalculationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * @author aarsatyants on 2018M3.
 */
public class TfsRatingClientTest {


    private static final String QUOTE_PATH1 = "response.json";


    @Test
    public void testDeserializer() throws IOException {
        String file = readFile(QUOTE_PATH1);
        ObjectMapper objectMapper = new ObjectMapper();
        DefaultTfsCalculationResult defaultTfsCalculationResult =
                objectMapper.readValue(file, DefaultTfsCalculationResult.class);
        assertTrue(defaultTfsCalculationResult.getPremiumAttributes().size() > 0);
    }

    public static String readFile(String path) {
        try {
            return IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            throw new InvocationError("check path configuration");
        }
    }

}
