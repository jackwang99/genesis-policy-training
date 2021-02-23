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
import com.eisgroup.genesis.report.ReportOutput.VersionedUsing;

/**
 * Line of business hive table representation for {@link PolicyTransactionActivityReport}
 *
 * @author mguzelis
 */
@TableName("LineOfBusiness")
@VersionedUsing(LineOfBusinessVersioningStrategy.class)
public class LineOfBusinessOutput extends ReportAggregate {
    private static final long serialVersionUID = 2325679891415003653L;

    private Timestamp updatedOn;
    private String lobCd;
    private Long numberOfRiskItems = 0L;
    private String blobCd;

    public LineOfBusinessOutput(PolicyTransactionActivityReportOutput root, LineOfBusinessRow lineOfBusiness) {
        this.rootId = root.getRootId();
        this.revisionNo = root.getRevisionNo();
        this.timestamp = root.getTimestamp();
        this.updatedOn = root.getUpdatedOn();
        this.blobCd = root.getBlobCd();
        this.parentId = this.rootId;
        this.id = lineOfBusiness.getId();
        this.lobCd = lineOfBusiness.getLobCd();
    }

    public Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Timestamp updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getLobCd() {
        return lobCd;
    }

    public void setLobCd(String lobCd) {
        this.lobCd = lobCd;
    }

    public Long getNumberOfRiskItems() {
        return numberOfRiskItems;
    }

    public void setNumberOfRiskItems(Long numberOfRiskItems) {
        this.numberOfRiskItems = numberOfRiskItems;
    }

    public String getBlobCd() {
        return blobCd;
    }

    public void setBlobCd(String blobCd) {
        this.blobCd = blobCd;
    }
}
