/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.policy.report;

import java.sql.Timestamp;

import com.eisgroup.genesis.policy.report.policy.PolicyTransactionActivityReport;
import com.eisgroup.genesis.policy.report.policy.PolicyTransactionActivityReportOutput;
import com.eisgroup.genesis.report.ReportOutput.ReportAggregate;
import com.eisgroup.genesis.report.ReportOutput.TableName;

/**
 * Premium entry hive table representation for {@link PolicyTransactionActivityReport}
 *
 * @author mguzelis
 */
@TableName("PremiumEntry")
public class PremiumEntryOutput extends ReportAggregate {
    private static final long serialVersionUID = 8530256855614541475L;

    private Timestamp updatedOn;
    private String premiumType;
    private Double changeAmount;
    private Double actualAmount;
    private Double termAmount;

    public PremiumEntryOutput(PolicyTransactionActivityReportOutput root, PremiumEntry premiumEntry, Long premiumEntryIndex) {
        this.rootId = root.getRootId();
        this.revisionNo = root.getRevisionNo();
        this.parentId = root.getRootId();
        this.id = premiumEntryIndex.toString();
        this.updatedOn = root.getUpdatedOn();
        this.timestamp = root.getTimestamp();
        this.premiumType = premiumEntry.getPremiumType();
        this.changeAmount = premiumEntry.getChangeAmount().getNumberStripped().doubleValue();
        this.actualAmount = premiumEntry.getActualAmount().getNumberStripped().doubleValue();
        this.termAmount = premiumEntry.getTermAmount().getNumberStripped().doubleValue();
    }

    public Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Timestamp updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getPremiumType() {
        return premiumType;
    }

    public void setPremiumType(String premiumType) {
        this.premiumType = premiumType;
    }

    public Double getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(Double changeAmount) {
        this.changeAmount = changeAmount;
    }

    public Double getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(Double actualAmount) {
        this.actualAmount = actualAmount;
    }

    public Double getTermAmount() {
        return termAmount;
    }

    public void setTermAmount(Double termAmount) {
        this.termAmount = termAmount;
    }

}
