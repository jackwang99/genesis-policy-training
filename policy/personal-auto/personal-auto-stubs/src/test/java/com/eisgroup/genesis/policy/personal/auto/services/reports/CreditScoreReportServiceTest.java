/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.services.reports;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.eisgroup.genesis.policy.personal.auto.services.reports.CreditScoreReportService;
import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.CreditScoreReportResponse;
import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.DriverReportRequest;

/**
 * Unit test for {@code CreditScoreReportService} class.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class CreditScoreReportServiceTest {

    private CreditScoreReportService testObject;

    @Before
    public void setUp() {
        testObject = new CreditScoreReportService();
    }

    @Test
    public void shouldReturnCreditScoreDataForDriver() {
        CreditScoreReportResponse ccr = testObject.orderReport(new DriverReportRequest("D123456"));

        Assert.assertTrue("690".equals(ccr.getScore()));
    }

}
