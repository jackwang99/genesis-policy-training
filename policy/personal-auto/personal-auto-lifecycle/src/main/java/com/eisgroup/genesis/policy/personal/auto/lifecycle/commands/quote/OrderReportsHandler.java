/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.eisgroup.genesis.factory.modeling.types.AutoPolicyParty;
import com.eisgroup.genesis.factory.modeling.types.AutoPolicySummary;
import com.eisgroup.genesis.factory.modeling.types.ClaimInfo;
import com.eisgroup.genesis.factory.modeling.types.CreditScoreInfo;
import com.eisgroup.genesis.factory.modeling.types.DriverInfo;
import com.eisgroup.genesis.factory.modeling.types.InsuredInfo;
import com.eisgroup.genesis.factory.modeling.types.LicenseInfo;
import com.eisgroup.genesis.factory.modeling.types.PriorCarrierInfo;
import com.eisgroup.genesis.factory.modeling.types.SuspensionInfo;
import com.eisgroup.genesis.factory.modeling.types.ViolationInfo;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.PersonalAutoCommands;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;

import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.policy.pnc.auto.lifecycle.commands.quote.request.OrderReportsRequest;
import com.eisgroup.genesis.policy.pnc.auto.services.reports.DriverReportService;
import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.ClueReportResponse;
import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.CreditScoreReportResponse;
import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.DriverReportRequest;
import com.eisgroup.genesis.policy.personal.auto.services.reports.dto.MotorVehicleReportResponse;
import com.eisgroup.genesis.policy.core.lifecycle.commands.quote.QuoteCommandHandler;

import io.reactivex.Single;

/**
 * Command handler for auto quote 'Order Reports' action.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class OrderReportsHandler extends QuoteCommandHandler<OrderReportsRequest, AutoPolicySummary> {

    @Autowired
    @Qualifier("motorVehicleReportService")
    private DriverReportService<MotorVehicleReportResponse, DriverReportRequest> mvrReportService;

    @Autowired
    @Qualifier("clueReportService")
    private DriverReportService<ClueReportResponse, DriverReportRequest> clueReportService;

    @Autowired
    @Qualifier("creditScoreReportService")
    private DriverReportService<CreditScoreReportResponse, DriverReportRequest> creditReportService;

    @Override
    public String getName() {
        return PersonalAutoCommands.ORDER_REPORTS;
    }

    @SuppressWarnings("squid:S1481")
    @Override
    public Single<AutoPolicySummary> execute(OrderReportsRequest request, AutoPolicySummary context) {
        return Single.just(context).map(quoteCtx -> {
            Optional.ofNullable(quoteCtx.getParties()).ifPresent(parties -> {
                DomainModel model = modelResolver.resolveModel(DomainModel.class);
                parties.stream()
                        .filter(party -> party.getDriverInfo() != null)
                        .map(party -> (AutoPolicyParty) party)
                        .collect(Collectors.toList())
                        .forEach(driver -> {
                            orderClue(driver, model);
                            orderMvr(driver, model);
                            orderCredit(driver, model);
                            driver.getDriverInfo().setReportsOrdered(true);
                        });
            });

            return quoteCtx;
        });
    }

    private void orderMvr(AutoPolicyParty driver, DomainModel model) {
        getDriverLicenseNumbers(driver)
                .forEach(licenseNo -> {
                    MotorVehicleReportResponse mvr = mvrReportService.orderReport(new DriverReportRequest(licenseNo));
                    if (mvr != null) {
                        List<SuspensionInfo> suspensions = new ArrayList<>();

                        mvr.getSuspensions().forEach(s -> suspensions.add(createSuspension(model, s)));

                        List<ViolationInfo> violations = new ArrayList<>();

                        mvr.getViolations().forEach(v -> violations.add(createViolation(model, v)));

                        driver.getDriverInfo().setSuspensions(suspensions);
                        driver.getDriverInfo().setViolations(violations);
                        updateDriverLicenseInfo(driver.getDriverInfo(), mvr);
                    }
                });
    }

    private void updateDriverLicenseInfo(DriverInfo driverInfo, MotorVehicleReportResponse mvr) {
        final Collection<LicenseInfo> licenseInfos = driverInfo.getLicenseInfo();
        if (licenseInfos != null) {
            licenseInfos.stream()
                    .filter(li -> Objects.equals(li.getLicenseNumber(), mvr.getDrivelLicenceNo()))
                    .forEach(li -> li.setLicenseStatusCd(mvr.getLicenseStatus()));
        }
    }

    private void orderClue(AutoPolicyParty driver, DomainModel model) {
        getDriverLicenseNumbers(driver)
                .forEach(licenseNo -> {
                    ClueReportResponse clue = clueReportService.orderReport(new DriverReportRequest(licenseNo));

                    if (clue != null && clue.getCarrier() != null) {
                        driver.setPriorCarrierInfo(createPriorCarrier(model, clue.getCarrier()));
                    }

                    if (clue != null && clue.getClaims() != null) {
                        List<ClaimInfo> claims = new ArrayList<>();

                        clue.getClaims().forEach(c -> claims.add(createClaimInfo(model, c)));

                        driver.getDriverInfo().setClaims(claims);
                    }
                });
    }

    private void orderCredit(AutoPolicyParty driver, DomainModel model) {
        getDriverLicenseNumbers(driver)
                .forEach(licenseNo -> {
                    if (licenseNo != null && isMainInsured(driver)) {
                        CreditScoreReportResponse ccr = creditReportService.orderReport(new DriverReportRequest(licenseNo));

                        if (ccr != null) {
                            driver.setCreditScoreInfo(createCreditScore(model, ccr));
                        }
                    }
                });
    }

    private boolean isMainInsured(AutoPolicyParty driver) {
        InsuredInfo insuredInfo = driver.getInsuredInfo();

        if (insuredInfo != null) {
            return BooleanUtils.isTrue(insuredInfo.getPrimary());
        }

        return false;
    }

    private List<String> getDriverLicenseNumbers(AutoPolicyParty driver) {
        return Optional.ofNullable(driver.getDriverInfo())
                .map(com.eisgroup.genesis.factory.modeling.types.immutable.DriverInfo::getLicenseInfo)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(com.eisgroup.genesis.factory.modeling.types.immutable.LicenseInfo::getLicenseNumber)
                .filter(ln -> !StringUtils.isEmpty(ln))
                .collect(Collectors.toList());
    }

    private ClaimInfo createClaimInfo(DomainModel model, ClueReportResponse.Claim claim) {
        ClaimInfo claimInfo =
                ModelInstanceFactory.createInstanceByBusinessType(model.getName(), model.getVersion(), ClaimInfo.NAME);

        claimInfo.setClaimType(claim.getClaimType());
        claimInfo.setDateOfLoss(claim.getDateOfLoss());
        claimInfo.setDescription(claim.getDescription());
        claimInfo.setTotalClaimCost(claim.getTotalClaimCost());
        claimInfo.setClaimStatus(claim.getClaimStatus());
        claimInfo.setLossType(claim.getLossType());

        return claimInfo;
    }

    private PriorCarrierInfo createPriorCarrier(DomainModel model, ClueReportResponse.Carrier carrier) {
        PriorCarrierInfo priorCarrierInfo =
                ModelInstanceFactory.createInstanceByBusinessType(model.getName(), model.getVersion(), PriorCarrierInfo.NAME);

        priorCarrierInfo.setCarrierName(carrier.getCarrierName());
        priorCarrierInfo.setStatus(carrier.getStatus());
        priorCarrierInfo.setDeductibles(carrier.getDeductibles());
        priorCarrierInfo.setLimitsBiPd(carrier.getLimitsBiPd());
        priorCarrierInfo.setCarrierPolicyNo(carrier.getCarrierPolicyNo());

        return priorCarrierInfo;
    }

    private CreditScoreInfo createCreditScore(DomainModel model, CreditScoreReportResponse creditScore) {
        CreditScoreInfo creditScoreInfo =
                ModelInstanceFactory.createInstanceByBusinessType(model.getName(), model.getVersion(), CreditScoreInfo.NAME);

        creditScoreInfo.setScore(creditScore.getScore());

        return creditScoreInfo;
    }

    private ViolationInfo createViolation(DomainModel model, MotorVehicleReportResponse.Violation violation) {
        ViolationInfo violationInfo =
                ModelInstanceFactory.createInstanceByBusinessType(model.getName(), model.getVersion(), ViolationInfo.NAME);

        violationInfo.setViolationType(violation.getViolationType());
        violationInfo.setViolationCode(violation.getViolationCode());
        violationInfo.setViolationCodeDesc(violation.getViolationCodeDesc());
        violationInfo.setViolationPoints(violation.getViolationPoints());
        violationInfo.setConvictionDate(violation.getConvictionDate());

        return violationInfo;
    }

    private SuspensionInfo createSuspension(DomainModel model, MotorVehicleReportResponse.Suspension suspension) {
        SuspensionInfo suspensionInfo =
                ModelInstanceFactory.createInstanceByBusinessType(model.getName(), model.getVersion(), SuspensionInfo.NAME);

        suspensionInfo.setReinstatementDate(suspension.getReinstatementDate());
        suspensionInfo.setSuspensionDate(suspension.getSuspensionDate());
        suspensionInfo.setViolationCode(suspension.getViolationCode());
        suspensionInfo.setViolationCodeDesc(suspension.getViolationCodeDesc());
        suspensionInfo.setViolationPoints(suspension.getViolationPoints());
        suspensionInfo.setViolationType(suspension.getViolationType());

        return suspensionInfo;
    }

    @Nonnull
    @Override
    public Single<AutoPolicySummary> load(@Nonnull final OrderReportsRequest request) {
        return Single.just(request.getQuote());
    }
}