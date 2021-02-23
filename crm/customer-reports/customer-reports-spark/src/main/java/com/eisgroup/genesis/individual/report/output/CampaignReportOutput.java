/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.individual.report.output;

import java.sql.Timestamp;
import java.util.Optional;

import com.eisgroup.genesis.factory.modeling.types.immutable.AccessTrackInfo;
import com.eisgroup.genesis.individual.report.input.CampaignRow;
import com.eisgroup.genesis.report.ReportOutput.ReportRoot;
import com.eisgroup.genesis.report.ReportOutput.TableName;

/**
 * Hive structure for campaign report output (root table)
 *
 * @author mguzelis
 */
@TableName("Campaign")
public class CampaignReportOutput extends ReportRoot {
    private static final long serialVersionUID = 9059789550514518337L;

    private Timestamp updatedOn;
    private String name;
    private String state;

    public CampaignReportOutput(CampaignRow campaign) {
        this.rootId = campaign.getCampaignId();
        this.revisionNo = campaign.getRevisionNo();
        this.timestamp = Optional.ofNullable(campaign.getTimestamp())
                .orElse(null);

        this.name = campaign.getName();
        this.state = campaign.getState();

        AccessTrackInfo accessTrackInfo = campaign.getAccessTrackInfo().getEntity();
        if (accessTrackInfo != null) {
            this.updatedOn = Timestamp.valueOf(accessTrackInfo.getUpdatedOn());
        }
    }

    public Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }
}
