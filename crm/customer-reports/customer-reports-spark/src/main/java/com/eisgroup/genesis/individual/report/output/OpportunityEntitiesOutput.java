/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.individual.report.output;

import java.sql.Timestamp;

import com.eisgroup.genesis.factory.model.opportunity.immutable.GenesisEntityAssociation;
import com.eisgroup.genesis.report.ReportOutput.ReportAggregate;
import com.eisgroup.genesis.report.ReportOutput.TableName;

/**
 * Opportunity entities hive table representation
 *
 * @author mguzelis
 */
@TableName("OpportunityEntities")
public class OpportunityEntitiesOutput extends ReportAggregate {
    private static final long serialVersionUID = 7871788806302172039L;

    private Timestamp updatedOn;
    private String entityNumber;
    private String entityType;
    private Double actualPremium;

    public OpportunityEntitiesOutput(OpportunityReportOutput root, GenesisEntityAssociation entity) {
        this.timestamp = root.getTimestamp();
        this.rootId = root.getRootId();
        this.revisionNo = root.getRevisionNo();
        this.parentId = root.getRootId();
        this.id = String.valueOf(entity.getKey().getId());

        this.updatedOn = root.getUpdatedOn();
        this.entityNumber = entity.getEntityNumber();

        if (entity.getLink() != null) {
            this.entityType = entity.getLink().getURI().getHost();
        }

        if (entity.getActualPremium() != null) {
            this.actualPremium = entity.getActualPremium().doubleValue();
        }
    }

    public Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public String getEntityNumber() {
        return entityNumber;
    }

    public String getEntityType() {
        return entityType;
    }

    public Double getActualPremium() {
        return actualPremium;
    }
}
