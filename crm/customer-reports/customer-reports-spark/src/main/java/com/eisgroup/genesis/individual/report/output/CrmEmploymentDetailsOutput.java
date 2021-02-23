/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws. 
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.eisgroup.genesis.individual.report.output;

import com.eisgroup.genesis.factory.model.individualcustomer.immutable.GenesisCrmEmploymentDetails;
import com.eisgroup.genesis.report.ReportOutput.ReportAggregate;
import com.eisgroup.genesis.report.ReportOutput.TableName;

/**
 * Individual employment details hive table representation
 * 
 * @author azukovskij
 *
 */
@TableName("EmploymentDetails")
public class CrmEmploymentDetailsOutput extends ReportAggregate {

    private static final long serialVersionUID = 3815654111897817549L;
    
    private String employerName;
    private String occupationCd;
    private String occupationStatusCd;

    public CrmEmploymentDetailsOutput() {
    }
    
    public CrmEmploymentDetailsOutput(CustomerReportOutput root, GenesisCrmEmploymentDetails employmentDetail) {
        this.timestamp = root.getTimestamp();
        this.rootId = root.getRootId();
        this.revisionNo = root.getRevisionNo();
        this.parentId = root.getRootId();
        this.id = String.valueOf(employmentDetail.getKey().getId());

        this.employerName = employmentDetail.getEmployerName();
        this.occupationCd = employmentDetail.getOccupationCd();
        this.occupationStatusCd = employmentDetail.getOccupationStatusCd();
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getOccupationCd() {
        return occupationCd;
    }

    public void setOccupationCd(String occupationCd) {
        this.occupationCd = occupationCd;
    }

    public String getOccupationStatusCd() {
        return occupationStatusCd;
    }

    public void setOccupationStatusCd(String occupationStatusCd) {
        this.occupationStatusCd = occupationStatusCd;
    }
    
}