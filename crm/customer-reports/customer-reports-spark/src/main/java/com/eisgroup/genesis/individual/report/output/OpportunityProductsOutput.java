/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.individual.report.output;

import java.sql.Timestamp;

import com.eisgroup.genesis.factory.model.opportunity.immutable.GenesisOpportunityProductInfo;
import com.eisgroup.genesis.report.ReportOutput.ReportAggregate;
import com.eisgroup.genesis.report.ReportOutput.TableName;

/**
 * Opportunity products hive table representation
 *
 * @author mguzelis
 */
@TableName("OpportunityProducts")
public class OpportunityProductsOutput extends ReportAggregate {
    private static final long serialVersionUID = 6581116508783733377L;

    private Timestamp updatedOn;
    private String productCd;

    public OpportunityProductsOutput(OpportunityReportOutput root, GenesisOpportunityProductInfo productInfo) {
        this.timestamp = root.getTimestamp();
        this.rootId = root.getRootId();
        this.revisionNo = root.getRevisionNo();
        this.parentId = root.getRootId();
        this.id = String.valueOf(productInfo.getKey().getId());

        this.updatedOn = root.getUpdatedOn();
        this.productCd = productInfo.getProductCd();
    }

    public Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public String getProductCd() {
        return productCd;
    }
}
