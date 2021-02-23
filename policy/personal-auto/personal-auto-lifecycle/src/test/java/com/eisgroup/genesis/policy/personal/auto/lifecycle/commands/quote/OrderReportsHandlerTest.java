/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import com.eisgroup.genesis.exception.InvocationError;
import com.eisgroup.genesis.factory.modeling.types.AutoPolicySummary;
import com.eisgroup.genesis.factory.modeling.types.immutable.AutoPolicyParty;
import com.eisgroup.genesis.factory.modeling.types.immutable.CreditScoreInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.InsuredInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.SuspensionInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.ViolationInfo;
import com.eisgroup.genesis.policy.core.lifecycle.commands.policy.AbstractHandlerTest;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.PersonalAutoCommands;
import com.eisgroup.genesis.policy.pnc.auto.lifecycle.commands.quote.request.OrderReportsRequest;
import com.eisgroup.genesis.policy.pnc.auto.services.reports.DriverReportService;
import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.ClueReportResponse;
import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.CreditScoreReportResponse;
import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.DriverReportRequest;
import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.MotorVehicleReportResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.reactivex.observers.TestObserver;
import org.apache.commons.lang3.BooleanUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Unit tests for {@code OrderReportsHandler} class.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class OrderReportsHandlerTest extends AbstractHandlerTest<AutoPolicySummary> {
    private static final String MODEL_NAME = "AutoTestProduct";

    private static final String QUOTE_PATH = "data/autoQuoteWithVehicles.json";
    private static final String POLICY_PATH = "data/policyWithRiskItems.json";

    private static final String MVR_RESPONSE_PATH = "data/mvr-details.json";
    private static final String CLUE_RESPONSE_PATH = "data/clue-details.json";
    private static final String CC_RESPONSE_PATH = "data/cc-details.json";

    @InjectMocks
    private OrderReportsHandler testObject;

    @Mock
    private DriverReportService<MotorVehicleReportResponse, DriverReportRequest> mvrReportService;

    @Mock
    private DriverReportService<ClueReportResponse, DriverReportRequest> clueReportService;

    @Mock
    private DriverReportService<CreditScoreReportResponse, DriverReportRequest> creditReportService;

    @Override
    @Before
    public void setUp() {
        testObject = new OrderReportsHandler();
        super.setUp();

        Mockito.when(mvrReportService.orderReport(Mockito.any(DriverReportRequest.class)))
                .thenReturn(createMvrResponse());
        Mockito.when(clueReportService.orderReport(Mockito.any(DriverReportRequest.class)))
                .thenReturn(createClueResponse());
        Mockito.when(creditReportService.orderReport(Mockito.any(DriverReportRequest.class)))
                .thenReturn(createCreditResponse());
    }

    @Test
    public void shouldFillInDriverSuspensionInfo() throws Exception {
        AutoPolicySummary policySummary = createAutoPolicy();
        MotorVehicleReportResponse mvr = createMvrResponse();

        TestObserver<AutoPolicySummary> executionResult =
            testObject.execute(new OrderReportsRequest(policySummary.toJson()), policySummary).test();

        AutoPolicySummary result = getEmittedPolicyFromContext(executionResult);

        result.getParties()
                .forEach(party -> {
                    Collection<SuspensionInfo> suspensions = party.getDriverInfo().getSuspensions();

                    Assert.assertTrue(suspensions.size() == 1);

                    SuspensionInfo suspensionInfo = suspensions.iterator().next();
                    MotorVehicleReportResponse.Suspension suspension = mvr.getSuspensions().get(0);

                    Assert.assertTrue(suspensionInfo.getViolationCode().equals(suspension.getViolationCode()));
                    Assert.assertTrue(suspensionInfo.getViolationCodeDesc().equals(suspension.getViolationCodeDesc()));
                    Assert.assertTrue(suspensionInfo.getViolationPoints().equals(suspension.getViolationPoints()));
                    Assert.assertTrue(suspensionInfo.getViolationType().equals(suspension.getViolationType()));
                    Assert.assertTrue(suspensionInfo.getReinstatementDate().equals(suspension.getReinstatementDate()));
                    Assert.assertTrue(suspensionInfo.getSuspensionDate().equals(suspension.getSuspensionDate()));
                });
    }

    @Test
    public void shouldFillInDriverViolationInfo() throws Exception {
        AutoPolicySummary policySummary = createAutoPolicy();
        MotorVehicleReportResponse mvr = createMvrResponse();

        TestObserver<AutoPolicySummary> executionResult =
            testObject.execute(new OrderReportsRequest(policySummary.toJson()), policySummary).test();

        AutoPolicySummary result = getEmittedPolicyFromContext(executionResult);

        result.getParties()
                .forEach(party -> {
                    Collection<ViolationInfo> violations = party.getDriverInfo().getViolations();

                    Assert.assertTrue(violations.size() == 1);

                    ViolationInfo violationInfo = violations.iterator().next();
                    MotorVehicleReportResponse.Violation violation = mvr.getViolations().get(0);

                    Assert.assertTrue(violationInfo.getViolationCode().equals(violation.getViolationCode()));
                    Assert.assertTrue(violationInfo.getViolationCodeDesc().equals(violation.getViolationCodeDesc()));
                    Assert.assertTrue(violationInfo.getViolationPoints().equals(violation.getViolationPoints()));
                    Assert.assertTrue(violationInfo.getViolationType().equals(violation.getViolationType()));
                    Assert.assertTrue(violationInfo.getConvictionDate().equals(violation.getConvictionDate()));
                });
    }

    @Test
    public void shouldFillInMainDriverCreditScoreInfoOnly() throws Exception {
        AutoPolicySummary policySummary = createAutoPolicy();
        CreditScoreReportResponse ccr = createCreditResponse();

        TestObserver<AutoPolicySummary> executionResult =
            testObject.execute(new OrderReportsRequest(policySummary.toJson()), policySummary).test();

        AutoPolicySummary result = getEmittedPolicyFromContext(executionResult);

        result.getParties()
                .forEach(party -> {
                    boolean isMainInsured = isMainInsured(party);

                    if (isMainInsured) {
                        Assert.assertTrue(party.getCreditScoreInfo() != null);

                        CreditScoreInfo creditScoreInfo = party.getCreditScoreInfo();

                        Assert.assertTrue(ccr.getScore().equals(creditScoreInfo.getScore()));
                    }
                    else {
                        Assert.assertTrue(party.getCreditScoreInfo() == null);
                    }
                });
    }

    @Test
    public void testGetName() {
        Assert.assertEquals(PersonalAutoCommands.ORDER_REPORTS, testObject.getName());
    }

    private JsonObject loadResource(String path) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
                InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return new JsonParser().parse(reader).getAsJsonObject();
        } catch (IOException e) {
            throw new InvocationError("Could not load test resource: " + path, e);
        }
    }

    private MotorVehicleReportResponse createMvrResponse() {
        return new MotorVehicleReportResponse(loadResource(MVR_RESPONSE_PATH));
    }

    private ClueReportResponse createClueResponse() {
        return new ClueReportResponse(loadResource(CLUE_RESPONSE_PATH));
    }

    private CreditScoreReportResponse createCreditResponse() {
        return new CreditScoreReportResponse(loadResource(CC_RESPONSE_PATH));
    }

    private AutoPolicySummary createAutoPolicy() {
        return toPolicySummary(loadResource(getQuoteFilePath()));
    }

    private boolean isMainInsured(AutoPolicyParty driver) {
        InsuredInfo insuredInfo = driver.getInsuredInfo();

        return insuredInfo != null && BooleanUtils.isTrue(insuredInfo.getPrimary());

    }

    @Override
    protected String getName() {
        return "orderReports";
    }

    @Override
    protected String getModelName() {
        return MODEL_NAME;
    }

    @Override
    protected String getQuoteFilePath() {
        return QUOTE_PATH;
    }

    @Override
    protected String getPolicyFilePath() {
        return POLICY_PATH;
    }

}
