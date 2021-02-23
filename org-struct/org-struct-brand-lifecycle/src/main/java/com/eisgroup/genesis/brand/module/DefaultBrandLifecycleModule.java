/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.brand.module;

/**
 * Default implementation of Brand lifecycle module.
 * 
 * @author adainelis
 * 
 */
public class DefaultBrandLifecycleModule extends BrandLifecycleModule {
    
    private static final String MODEL_NAME = "Brand";

    @Override
    public String getModelName() {
        return MODEL_NAME;
    }
    
}
