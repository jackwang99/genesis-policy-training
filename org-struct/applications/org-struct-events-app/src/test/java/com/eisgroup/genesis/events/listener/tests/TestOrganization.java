/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.events.listener.tests;

import com.eisgroup.genesis.factory.modeling.types.Organization;
import com.google.gson.JsonObject;

/**
 * @author gvisokinskas
 */
class TestOrganization implements Organization {

    private final JsonObject original = new JsonObject();

    @Override
    public JsonObject toJson() {
        return this.original;
    }

    @Override
    public String getModelName() {
        return null;
    }

    @Override
    public String getModelType() {
        return null;
    }

}
