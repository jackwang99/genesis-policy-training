/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.facade;

import com.eisgroup.genesis.policy.core.facade.PolicyFacade;

/**
 * The Personal Home Facade
 * 
 * @author zhanchen
 *
 */
public class PersonalHomeFacade extends PolicyFacade {

    @Override
    public String getModelName() {
        return "PersonalHome";
    }

    @Override
    public int getFacadeVersion() {
        return 1;
    }

}
