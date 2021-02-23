/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.modules;

import org.springframework.context.annotation.Configuration;

/**
 * @author dstulgis
 */
@Configuration
public class BusinessRelationshipLifecycleModule extends BusinessRelationshipModule {

    @Override
    public String getModelName() {
        return "BusinessRelationshipProduct";
    }

}
