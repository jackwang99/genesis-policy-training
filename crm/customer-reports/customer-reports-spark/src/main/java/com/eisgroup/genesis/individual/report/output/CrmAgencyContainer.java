/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws. 
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.eisgroup.genesis.individual.report.output;

import com.eisgroup.genesis.individual.report.input.AgencyContainerRow;
import com.eisgroup.genesis.report.ReportOutput.ReportAggregate;
import com.eisgroup.genesis.report.ReportOutput.TableName;

/**
 * Customer agency container hive table representation
 *
 * @author azukovskij
 */
@TableName("AgencyContainer")
public class CrmAgencyContainer extends ReportAggregate {

    private static final long serialVersionUID = 622560448724978088L;

    private String agency;

    public CrmAgencyContainer() {
    }

    public CrmAgencyContainer(CustomerReportOutput root, AgencyContainerRow container) {
        this.rootId = root.getRootId();
        this.revisionNo = root.getRevisionNo();
        this.parentId = root.getRootId();
        this.id = String.valueOf(container.getRootId());
        this.timestamp = root.getTimestamp();

        this.agency = container.getAgency();
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

}
