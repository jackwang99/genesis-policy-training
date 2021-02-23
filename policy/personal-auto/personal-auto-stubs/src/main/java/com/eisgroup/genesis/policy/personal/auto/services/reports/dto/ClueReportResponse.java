/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.services.reports.dto;

import com.eisgroup.genesis.json.AbstractJsonEntity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Driver's CLUE and Carrier report data.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class ClueReportResponse extends AbstractJsonEntity {

    private static final String MAIN_DRV_LICENSE_NO = "mainDriverLicenseNo";
    private static final String CARRIER_INFO = "carrierInfo";
    private static final String CLAIMS = "claims";

    public ClueReportResponse(JsonObject original) {
        super(original);
    }

    public String getMainDrvLicenseNo() {
        return getString(MAIN_DRV_LICENSE_NO);
    }

    public Carrier getCarrier() {
        JsonObject carrierInfo = getOriginal().getAsJsonObject(CARRIER_INFO);

        return new Carrier(carrierInfo);
    }

    public List<Claim> getClaims() {
        List<Claim> result = new ArrayList<>();
        JsonArray claims = getOriginal().getAsJsonArray(CLAIMS);

        if (claims != null) {
            claims.forEach(c -> result.add(new Claim(c.getAsJsonObject())));
        }

        return result;
    }

    public static class Carrier extends AbstractJsonEntity {

        private static final String CARRIER_NAME = "carrierName";
        private static final String STATUS = "status";
        private static final String LIMITS_BI_PD = "limitsBiPd";
        private static final String DEDUCTIBLES = "deductibles";
        private static final String CARRIER_NO = "carrierPolicyNo";

        public Carrier(JsonObject original) {
            super(original);
        }

        public String getCarrierName() {
            return getString(CARRIER_NAME);
        }

        public String getStatus() {
            return getString(STATUS);
        }

        public String getLimitsBiPd() {
            return getString(LIMITS_BI_PD);
        }

        public MonetaryAmount getDeductibles() {
            return getMonetaryAmount(DEDUCTIBLES);
        }

        public String getCarrierPolicyNo() {
            return getString(CARRIER_NO);
        }

    }

    public static class Claim extends AbstractJsonEntity {

        private static final String CLAIM_TYPE = "claimType";
        private static final String DATE_OF_LOSS = "dateOfLoss";
        private static final String DESCRIPTION = "description";
        private static final String TOTAL_CLAIM_COST = "totalClaimCost";
        private static final String CLAIM_STATUS = "claimStatus";
        private static final String LOSS_TYPE = "lossType";

        public Claim(JsonObject original) {
            super(original);
        }

        public String getClaimType() {
            return getString(CLAIM_TYPE);
        }

        public LocalDate getDateOfLoss() {
            return getDate(DATE_OF_LOSS);
        }

        public String getDescription() {
            return getString(DESCRIPTION);
        }

        public MonetaryAmount getTotalClaimCost() {
            return getMonetaryAmount(TOTAL_CLAIM_COST);
        }

        public String getClaimStatus() {
            return getString(CLAIM_STATUS);
        }

        public String getLossType() {
            return getString(LOSS_TYPE);
        }
    }

}
