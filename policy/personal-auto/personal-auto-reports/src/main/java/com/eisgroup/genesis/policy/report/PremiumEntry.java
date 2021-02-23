/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws. 
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.eisgroup.genesis.policy.report;

import org.javamoney.moneta.Money;

import com.eisgroup.genesis.json.AbstractJsonEntity;
import com.google.gson.JsonObject;

/**
 * DTO for holding premium entry data
 */
public class PremiumEntry extends AbstractJsonEntity {

    private static final String PREMIUM_CODE = "premiumCode";
    private static final String TERM_AMOUNT = "termAmount";
    private static final String CHANGE_AMOUNT = "changeAmount";
    private static final String ACTUAL_AMOUNT = "actualAmount";
    private static final String PREMIUM_TYPE = "premiumType";

    public PremiumEntry() {
        super(new JsonObject());
    }

    public PremiumEntry(JsonObject original) {
        super(original);
    }

    public String getPremiumCode() {
        return getString(PREMIUM_CODE);
    }

    public Money getTermAmount() {
        return (Money) getMonetaryAmount(TERM_AMOUNT);
    }

    public Money getChangeAmount() {
        return (Money) getMonetaryAmount(CHANGE_AMOUNT);
    }

    public Money getActualAmount() {
        return (Money) getMonetaryAmount(ACTUAL_AMOUNT);
    }

    public String getPremiumType() {
        return getString(PREMIUM_TYPE);
    }
}