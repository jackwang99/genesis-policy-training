/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.report.output;

import java.sql.Timestamp;

import com.eisgroup.genesis.factory.modeling.types.immutable.OrganizationRoleDetails;
import com.eisgroup.genesis.json.key.BaseKey;
import com.eisgroup.genesis.report.ReportOutput.ReportAggregate;
import com.eisgroup.genesis.report.ReportOutput.TableName;
import com.google.gson.JsonElement;

/**
 * Underwriting company hive table representation
 *
 * @author mguzelis
 */
@TableName("UnderwritingCompany")
public class UnderwritingCompanyOutput extends ReportAggregate {
    private static final long serialVersionUID = 8900291968857959766L;

    private static final String UNDERWRITING_COMPANY_CD = "underwritingCompanyCd";

    private Timestamp updatedOn;
    private String underwritingCompanyCd;

    public UnderwritingCompanyOutput(OrganizationReportOutput root, OrganizationRoleDetails organizationRoleDetails) {
        BaseKey key = organizationRoleDetails.getKey();
        this.rootId = String.valueOf(key.getRootId());
        this.parentId = this.rootId;
        this.id = String.valueOf(key.getId());
        this.revisionNo = key.getRevisionNo();
        this.updatedOn = root.getUpdatedOn();
        this.timestamp = root.getTimestamp();

        JsonElement underwritingCompanyCdJson = organizationRoleDetails.toJson().get(UNDERWRITING_COMPANY_CD);
        if (underwritingCompanyCdJson != null) {
            this.underwritingCompanyCd = underwritingCompanyCdJson.getAsString();
        }
    }

    public Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public String getUnderwritingCompanyCd() {
        return underwritingCompanyCd;
    }
}
