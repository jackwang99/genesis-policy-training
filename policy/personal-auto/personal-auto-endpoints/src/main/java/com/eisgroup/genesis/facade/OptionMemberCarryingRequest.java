/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.facade;

import com.eisgroup.genesis.facade.request.Body;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.QuoteCarryingRequest;
import com.google.gson.JsonObject;

/**
 * Option member key request
 *
 * @author aspichakou
 */
@Body
public class OptionMemberCarryingRequest extends QuoteCarryingRequest {

    public OptionMemberCarryingRequest(JsonObject original) {
        super(original);
    }

    public OptionMemberCarryingRequest(PolicySummary policySummary) {
        super(policySummary);
    }
}
