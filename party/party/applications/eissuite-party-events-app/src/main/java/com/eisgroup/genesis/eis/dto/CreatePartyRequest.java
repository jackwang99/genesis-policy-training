/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.eis.dto;

import java.util.List;

/**
 * @author dlevchuk
 */
public class CreatePartyRequest {

    private Reference parentNavigationReference;
    private List<EISParty> parties;

    public CreatePartyRequest(Reference parentNavigationReference, List<EISParty> parties) {
        this.parentNavigationReference = parentNavigationReference;
        this.parties = parties;
    }

    public CreatePartyRequest () {}

    public Reference getParentNavigationReference() {
        return parentNavigationReference;
    }

    public void setParentNavigationReference(Reference parentNavigationReference) {
        this.parentNavigationReference = parentNavigationReference;
    }

    public List<EISParty> getParties() {
        return parties;
    }

    public void setParties(List<EISParty> parties) {
        this.parties = parties;
    }
}
