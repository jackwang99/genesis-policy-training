/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.services.reports;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.eisgroup.genesis.policy.personal.auto.services.reports.MotorVehicleReportService;
import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.DriverReportRequest;
import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.MotorVehicleReportResponse;

/**
 * Unit tests for {@code MotorVehicleReportService} class.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class MotorVehicleReportServiceTest {

    private MotorVehicleReportService testObject;

    @Before
    public void setUp() {
        testObject = new MotorVehicleReportService();
    }

    @Test
    public void shouldReturnMotorVehicleReportForDriver() {
        MotorVehicleReportResponse mvr = testObject.orderReport(new DriverReportRequest("C123456"));

        Assert.assertTrue("C123456".equals(mvr.getDrivelLicenceNo()));
        Assert.assertTrue("Suspended".equals(mvr.getLicenseStatus()));

        Assert.assertTrue(mvr.getSuspensions().size() == 2);
        Assert.assertTrue(mvr.getViolations().size() == 1);
    }

}
