/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.services.reports.dto;

import com.eisgroup.genesis.json.AbstractJsonEntity;
import com.google.gson.JsonObject;

/**
 * Driver license request. Carries data needed to order reports
 * for a driver.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class DriverReportRequest extends AbstractJsonEntity {

    private static final String DRIVE_LICENSE_NO = "driverLicenseNo";

    public DriverReportRequest(JsonObject original) {
        super(original);
    }

    public DriverReportRequest(String driverLicenseNo) {
        super(new JsonObject());
        setString(DRIVE_LICENSE_NO, driverLicenseNo);
    }

    public String getDriveLicenseNo() {
        return getString(DRIVE_LICENSE_NO);
    }

}
