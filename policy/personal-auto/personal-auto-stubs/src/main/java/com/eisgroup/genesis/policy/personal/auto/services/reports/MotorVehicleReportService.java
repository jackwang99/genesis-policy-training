/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.services.reports;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.springframework.util.Assert;

import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.DriverReportRequest;
import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.MotorVehicleReportResponse;
import com.eisgroup.genesis.policy.pnc.auto.services.reports.DriverReportService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Implementation of {@code DriverReportService} which returns stub motor vehicle
 * report data.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class MotorVehicleReportService implements DriverReportService<MotorVehicleReportResponse, DriverReportRequest> {

    private static final String MVR_DATA_PATH = "META-INF/stubs/mvr-stub.json";
    private static final String REPORTS = "reports";

    @Override
    public MotorVehicleReportResponse orderReport(DriverReportRequest request) {
        Assert.notNull(request, "request should not be null!");

        return findReportForDriver(getReportData(), request.getDriveLicenseNo());
    }

    private MotorVehicleReportResponse findReportForDriver(JsonObject data, String driverLicenseNo) {
        JsonArray reports = data.getAsJsonArray(REPORTS);

        MotorVehicleReportResponse mvr;

        for (JsonElement report : reports) {
            mvr = new MotorVehicleReportResponse(report.getAsJsonObject());

            if (driverLicenseNo.equals(mvr.getDrivelLicenceNo())) {
                return mvr;
            }
        }

        return null;
    }

    private JsonObject getReportData() {
        JsonParser parser = new JsonParser();

        InputStream is = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(MVR_DATA_PATH);

        return parser.parse(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
    }

}
