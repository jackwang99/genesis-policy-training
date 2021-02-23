/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.services.reports.dto;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import com.eisgroup.genesis.modules.utils.TestProductConstants;
import org.javamoney.moneta.Money;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.eisgroup.genesis.exception.InvocationError;
import com.eisgroup.genesis.util.DateUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Unit tests for {@code ClueReportResponse} class.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class ClueReportResponseTest {

    private ClueReportResponse testObject;

    @Before
    public void setUp() {
        testObject = new ClueReportResponse(readClueDetails());
    }

    @Test
    public void shouldReturnClueReportData() {
        Assert.assertTrue("D123456".equals(testObject.getMainDrvLicenseNo()));
        Assert.assertTrue(testObject.getCarrier() != null);

        ClueReportResponse.Carrier carrieInfo = testObject.getCarrier();

        Assert.assertTrue("Progreico".equals(carrieInfo.getCarrierName()));
        Assert.assertTrue("Cancelled".equals(carrieInfo.getStatus()));
        Assert.assertTrue("50/100/50".equals(carrieInfo.getLimitsBiPd()));
        Assert.assertThat(carrieInfo.getDeductibles(),equalTo(Money.of(BigDecimal.valueOf(500), TestProductConstants.CURRENCY_CODE_USD)));
        Assert.assertTrue("G7700004".equals(carrieInfo.getCarrierPolicyNo()));

        Assert.assertTrue(testObject.getClaims().size() == 1);

        ClueReportResponse.Claim claimInfo = testObject.getClaims().get(0);

        Assert.assertTrue(DateUtil.toDate("2016-08-08").equals(claimInfo.getDateOfLoss()));
        Assert.assertTrue("Comprehensive".equals(claimInfo.getClaimType()));
        Assert.assertThat(claimInfo.getTotalClaimCost(),equalTo(Money.of(BigDecimal.valueOf(2000), TestProductConstants.CURRENCY_CODE_USD)));
        Assert.assertEquals("initiated", claimInfo.getClaimStatus());
        Assert.assertEquals("driving accident", claimInfo.getDescription());
        Assert.assertEquals("type of loss", claimInfo.getLossType());
    }

    private JsonObject readClueDetails() {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("data/clue-details.json");
                InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return new JsonParser().parse(reader).getAsJsonObject();
        }
        catch (IOException e) {
            throw new InvocationError("Unable to load required resource", e);
        }
    }
}
