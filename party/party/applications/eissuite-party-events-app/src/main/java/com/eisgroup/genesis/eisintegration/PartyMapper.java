/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.eisintegration;

import com.eisgroup.genesis.eis.dto.EISParty;
import com.eisgroup.genesis.events.handlers.EISSuitePartyEventHandler;

/**
 * Maps the genesis party to the corresponding EIS party
 *
 * @author dlevchuk
 */
public interface PartyMapper {

    /**
     * Maps the genesis party to the corresponding EIS party
     *
     * @param party Genesis party
     * @return the corresponding is party, or null if there type is unsupported
     */
    EISParty map(EISSuitePartyEventHandler.PartyPayload party);
}
