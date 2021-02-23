/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.json.reducer.ref.app.services.boot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.eisgroup.genesis.json.reducer.ref.app.boot.JsonReducerBootstrap;

/**
 * @author ssauchuk
 * @since 10.9
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JsonReducerBootstrap.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class JsonReducerBootstrapTest {

    @Test
    public void shouldApplicationContextInitialized() {
        //check application context initialisation
    }
}
