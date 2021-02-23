/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.commands.customer.request;

import com.eisgroup.genesis.common.Constraint;
import com.eisgroup.genesis.crm.commands.request.CustomerWriteRequest;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.google.gson.JsonObject;

import java.util.UUID;

/**
 * @author Valeriy Sizonenko
 * @since 10.4
 */
public class CustomerSplitRequest extends CustomerWriteRequest {

    private static final String SPLIT_FROM_ID = "splitFromId";

    public CustomerSplitRequest(JsonObject original) {
        super(original);
    }

    public CustomerSplitRequest(UUID splitFromId, Customer customer) {
        super(customer);
        setUUID(SPLIT_FROM_ID, splitFromId);
    }

    @Constraint(required = true)
    public UUID getSplitFromId() {
        return getUUID(SPLIT_FROM_ID);
    }
}