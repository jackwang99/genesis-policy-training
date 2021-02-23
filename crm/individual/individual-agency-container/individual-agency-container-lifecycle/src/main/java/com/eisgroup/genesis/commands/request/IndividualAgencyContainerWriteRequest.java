/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.commands.request;

import com.eisgroup.genesis.factory.modeling.types.IndividualAgencyContainer;
import com.google.gson.JsonObject;

/**
 * @author Dmitry Andronchik
 */
public class IndividualAgencyContainerWriteRequest extends AgencyContainerWriteRequest<IndividualAgencyContainer> {

    public IndividualAgencyContainerWriteRequest(JsonObject original) {
        super(original);
    }

    public IndividualAgencyContainerWriteRequest(IndividualAgencyContainer agencyContainer) {
        super(agencyContainer);
    }
}
