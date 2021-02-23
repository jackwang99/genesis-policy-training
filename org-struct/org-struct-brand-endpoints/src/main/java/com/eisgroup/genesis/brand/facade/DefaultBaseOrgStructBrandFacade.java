/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.brand.facade;

/**
 * Default implementation of Brand related endpoints facade.
 * 
 * @author adainelis
 * 
 */

public class DefaultBaseOrgStructBrandFacade extends BaseOrgStructBrandFacade {

    private static final String MODEL_NAME = "Brand";
    
    @Override
    public String getModelName() {
        return MODEL_NAME;
    }

    @Override
    public int getFacadeVersion() {
        return 1;
    }
    
}
