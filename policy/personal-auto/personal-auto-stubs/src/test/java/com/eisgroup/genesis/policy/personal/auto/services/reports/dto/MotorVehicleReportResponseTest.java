/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.services.reports.dto;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.eisgroup.genesis.exception.InvocationError;
import com.eisgroup.genesis.util.DateUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Unit tests for {@code MotorVehicleReportResponse} class.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class MotorVehicleReportResponseTest {

    private MotorVehicleReportResponse testObject;

    @Before
    public void setUp() {
        JsonObject mvr = readMvrDetails();
        testObject = new MotorVehicleReportResponse(mvr);
    }

    @Test
    public void shouldReturnMotorVehicleReportData() {
        Assert.assertTrue(testObject.getDrivelLicenceNo().equals("C123456"));
        Assert.assertTrue(testObject.getLicenseStatus().equals("Suspended"));

        List<MotorVehicleReportResponse.Suspension> suspensions = testObject.getSuspensions();

        Assert.assertTrue(suspensions.size() == 1);

        MotorVehicleReportResponse.Suspension suspension = suspensions.get(0);
        Assert.assertTrue(DateUtil.toDate("2015-06-13").equals(suspension.getSuspensionDate()));
        Assert.assertTrue(DateUtil.toDate("2016-06-06").equals(suspension.getReinstatementDate()));
        Assert.assertTrue("16220".equals(suspension.getViolationCode()));
        Assert.assertTrue("ALCOHOL/DRUG WITHDRAWAL".equals(suspension.getViolationCodeDesc()));
        Assert.assertTrue("Suspension".equals(suspension.getViolationType()));
        Assert.assertTrue("5".equals(suspension.getViolationPoints()));

        List<MotorVehicleReportResponse.Violation> violations = testObject.getViolations();

        Assert.assertTrue(suspensions.size() == 1);

        MotorVehicleReportResponse.Violation violation = violations.get(0);
        Assert.assertTrue(DateUtil.toDate("2015-06-13").equals(violation.getConvictionDate()));
        Assert.assertTrue("52100".equals(violation.getViolationCode()));
        Assert.assertTrue("DUI, GENERALLY".equals(violation.getViolationCodeDesc()));
        Assert.assertTrue("Driving Under Influence".equals(violation.getViolationType()));
        Assert.assertTrue("5".equals(violation.getViolationPoints()));
    }

    private JsonObject readMvrDetails() {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("data/mvr-details.json");
                InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return new JsonParser().parse(reader).getAsJsonObject();
        }
        catch (IOException e) {
            throw new InvocationError("Unable to load required resource", e);
        }
    }
}
