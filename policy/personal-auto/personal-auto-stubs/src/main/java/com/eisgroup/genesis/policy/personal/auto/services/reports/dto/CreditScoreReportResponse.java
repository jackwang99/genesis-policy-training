/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.services.reports.dto;

import com.eisgroup.genesis.json.AbstractJsonEntity;
import com.google.gson.JsonObject;

/**
 * Driver's credit score report data.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class CreditScoreReportResponse extends AbstractJsonEntity {

    private static final String SCORE = "score";

    public CreditScoreReportResponse(JsonObject original) {
        super(original);
    }

    public String getScore() {
        return getString(SCORE);
    }

}
