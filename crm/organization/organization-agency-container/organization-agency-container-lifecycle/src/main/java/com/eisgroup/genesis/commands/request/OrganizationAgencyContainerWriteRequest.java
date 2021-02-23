/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.commands.request;

import com.eisgroup.genesis.factory.modeling.types.OrganizationAgencyContainer;
import com.google.gson.JsonObject;

/**
 * @author Dmitry Andronchik
 */
public class OrganizationAgencyContainerWriteRequest extends AgencyContainerWriteRequest<OrganizationAgencyContainer> {

    public OrganizationAgencyContainerWriteRequest(JsonObject original) {
        super(original);
    }

    public OrganizationAgencyContainerWriteRequest(OrganizationAgencyContainer agencyContainer) {
        super(agencyContainer);
    }
}
