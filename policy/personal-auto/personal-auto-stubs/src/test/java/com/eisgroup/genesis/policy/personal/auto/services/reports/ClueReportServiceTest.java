/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.services.reports;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.eisgroup.genesis.policy.personal.auto.services.reports.ClueReportService;
import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.ClueReportResponse;
import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.DriverReportRequest;

/**
 * Unit tests for {@code ClueReportService} class.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class ClueReportServiceTest {

    private ClueReportService testObject;

    @Before
    public void setUp() {
        testObject = new ClueReportService();
    }

    @Test
    public void shouldReturnClueDateForDriver() {
        ClueReportResponse clue = testObject.orderReport(new DriverReportRequest("D123456"));

        Assert.assertTrue("D123456".equals(clue.getMainDrvLicenseNo()));
        Assert.assertTrue(clue.getCarrier() != null);
        Assert.assertTrue(clue.getClaims().size() == 3);
    }

}
