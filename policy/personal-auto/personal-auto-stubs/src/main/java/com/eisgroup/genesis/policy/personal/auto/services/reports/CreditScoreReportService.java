/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.services.reports;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.springframework.util.Assert;

import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.CreditScoreReportResponse;
import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.DriverReportRequest;
import com.eisgroup.genesis.policy.pnc.auto.services.reports.DriverReportService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Implementation of {@code DriverReportService} which returns stub credit score
 * report data.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class CreditScoreReportService implements DriverReportService<CreditScoreReportResponse, DriverReportRequest> {

    private static final String CC_DATA_PATH = "META-INF/stubs/cc-stub.json";
    private static final String REPORTS = "reports";
    private static final String DRV_LICENSE_NO = "driverLicenseNo";

    @Override
    public CreditScoreReportResponse orderReport(DriverReportRequest request) {
        Assert.notNull(request, "request should not be null!");

        return findReportForDriver(getReportData(), request.getDriveLicenseNo());
    }

    private CreditScoreReportResponse findReportForDriver(JsonObject data, String driverLicenseNo) {
        JsonArray reports = data.getAsJsonArray(REPORTS);

        CreditScoreReportResponse ccr;

        for (JsonElement report : reports) {
            ccr = new CreditScoreReportResponse(report.getAsJsonObject());

            if (getDrivelLicenceNo(report.getAsJsonObject()).equals(driverLicenseNo)) {
                return ccr;
            }
        }

        return null;
    }

    private String getDrivelLicenceNo(JsonObject report) {
        JsonElement licenseNo = report.get(DRV_LICENSE_NO);

        return licenseNo != null ? licenseNo.getAsString() : "";
    }

    private JsonObject getReportData() {
        JsonParser parser = new JsonParser();

        InputStream is = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(CC_DATA_PATH);

        return parser.parse(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
    }

}
