/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.infra.reports.output;

import java.sql.Timestamp;
import java.util.Optional;

import com.eisgroup.genesis.infra.reports.BamActivityVersioningStrategy;
import com.eisgroup.genesis.infra.reports.input.ActivityAggregateRow;
import com.eisgroup.genesis.report.ReportOutput.ReportRoot;
import com.eisgroup.genesis.report.ReportOutput.TableName;
import com.eisgroup.genesis.report.ReportOutput.VersionedUsing;

/**
 * Hive structure for BAM activity report output (root table)
 *
 * @author mguzelis
 */
@TableName("Activity")
@VersionedUsing(BamActivityVersioningStrategy.class)
public class BamReportOutput extends ReportRoot {
    private static final long serialVersionUID = -1376400035823651784L;

    private Timestamp updatedOn;
    private String entityId;
    private String activityArea;
    private String activityName;
    private String entityNumber;
    private String performer;
    private String activityStatus;
    private Timestamp activityStartDate;
    private Timestamp activityEndDate;

    public BamReportOutput(ActivityAggregateRow activity) {
        this(activity.getId(), 1, activity.getTimestamp());

        this.updatedOn = activity.getTimestamp();
        this.entityId = activity.getEntityId();
        this.activityArea = activity.getGroup();
        this.activityName = activity.getMessageId();
        this.entityNumber = activity.getEntityNumber();
        this.performer = parsePerformerFromUserKey(activity.getUserKey());
        this.activityStatus = activity.getStatus();
        this.activityStartDate = activity.getStartedTime();
        this.activityEndDate = activity.getEndedTime();
    }

    private BamReportOutput(String rootId, Integer revisionNo, Timestamp timestamp) {
        this.rootId = rootId;
        this.revisionNo = revisionNo;
        this.timestamp = timestamp;
    }

    public Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getActivityArea() {
        return activityArea;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getEntityNumber() {
        return entityNumber;
    }

    public String getPerformer() {
        return performer;
    }

    public String getActivityStatus() {
        return activityStatus;
    }

    public Timestamp getActivityStartDate() {
        return activityStartDate;
    }

    public Timestamp getActivityEndDate() {
        return activityEndDate;
    }

    // example format of userKey: security://user/qa/738fce3c-4fc9-4c32-967a-75897f5b58ff
    private String parsePerformerFromUserKey(String userKey) {
        return Optional.ofNullable(userKey)
                .map(keyString -> keyString.split("/"))
                .filter(split -> split.length == 5)
                .map(split -> split[3])
                .orElse(null);
    }

}
