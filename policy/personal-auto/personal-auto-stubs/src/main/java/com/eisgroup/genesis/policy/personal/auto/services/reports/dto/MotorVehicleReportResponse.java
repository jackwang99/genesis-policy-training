/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.services.reports.dto;

import com.eisgroup.genesis.json.AbstractJsonEntity;
import com.eisgroup.genesis.util.GsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Motor vehicle report response data.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class MotorVehicleReportResponse extends AbstractJsonEntity {

    private static final String DRIVEL_LICENCE_NO = "driverLicenseNo";
    private static final String LICENSE_STATUS = "licenseStatus";
    private static final String VIOLATIONS = "violations";
    private static final String SUSPENSIONS = "suspensions";

    public MotorVehicleReportResponse(JsonObject original) {
        super(original);
    }

    public String getDrivelLicenceNo() {
        return getString(DRIVEL_LICENCE_NO);
    }

    public String getLicenseStatus() {
        return getString(LICENSE_STATUS);
    }

    public List<Violation> getViolations() {
        List<Violation> result = new ArrayList<>();
        JsonArray violations = GsonUtil.getAsJsonArray(getOriginal(), VIOLATIONS);

        if (violations != null) {
            violations.forEach(v -> result.add(new Violation(v.getAsJsonObject())));
        }

        return result;
    }

    public List<Suspension> getSuspensions() {
        List<Suspension> result = new ArrayList<>();
        JsonArray suspensions = GsonUtil.getAsJsonArray(getOriginal(), SUSPENSIONS);

        if (suspensions != null) {
            suspensions.forEach(s -> result.add(new Suspension(s.getAsJsonObject())));
        }

        return result;
    }

    public abstract class Occurrence extends AbstractJsonEntity {

        private static final String VIOLATION_CODE = "violationCode";
        private static final String VIOLATION_CODE_DESC = "violationCodeDescription";
        private static final String VIOLATION_TYPE = "violationType";
        private static final String VIOLATION_POINTS = "violationPoints";

        public Occurrence(JsonObject original) {
            super(original);
        }

        public String getViolationCode() {
            return getString(VIOLATION_CODE);
        }

        public String getViolationCodeDesc() {
            return getString(VIOLATION_CODE_DESC);
        }

        public String getViolationType() {
            return getString(VIOLATION_TYPE);
        }

        public String getViolationPoints() {
            return getString(VIOLATION_POINTS);
        }

    }

    public class Violation extends Occurrence {

        private static final String CONVICTION_DATE = "convictionDate";

        public Violation(JsonObject original) {
            super(original);
        }

        public LocalDate getConvictionDate() {
            return getDate(CONVICTION_DATE);
        }

    }

    public class Suspension extends Occurrence {

        private static final String SUSPENSION_DATE = "suspensionDate";
        private static final String REINSTATEMENT_DATE = "reinstatementDate";

        public Suspension(JsonObject original) {
            super(original);
        }

        public LocalDate getSuspensionDate() {
            return getDate(SUSPENSION_DATE);
        }

        public LocalDate getReinstatementDate() {
            return getDate(REINSTATEMENT_DATE);
        }

    }

}
