/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.json.reducer.ref.app.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Stub integration example.
 *
 * @author ssauchuk
 * @since 10.2
 */
@SpringBootApplication(scanBasePackages = "com.eisgroup.genesis.json.reducer.ref.app.services")
public class JsonReducerBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(new Class[] { JsonReducerBootstrap.class}, args);
    }
}
