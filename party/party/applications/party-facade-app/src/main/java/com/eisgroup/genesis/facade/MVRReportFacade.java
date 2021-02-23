/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.facade;

import com.eisgroup.genesis.party.core.facade.PartyCoreFacade;

/**
 * Facade for {@link com.eisgroup.genesis.factory.model.mvrreport.MVRReportEntity}
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class MVRReportFacade extends PartyCoreFacade {

    @Override
    public String getModelName() {
        return "MVRReport";
    }

    @Override
    public int getFacadeVersion() {
        return 1;
    }

}
