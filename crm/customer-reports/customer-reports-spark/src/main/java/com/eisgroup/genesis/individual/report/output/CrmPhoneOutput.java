/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws. 
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.eisgroup.genesis.individual.report.output;

import java.sql.Date;

import com.eisgroup.genesis.factory.modeling.types.immutable.ConsentInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.CrmPhone;
import com.eisgroup.genesis.report.ReportOutput.ReportAggregate;
import com.eisgroup.genesis.report.ReportOutput.TableName;
import static com.eisgroup.genesis.report.util.DateUtils.*;

/**
 * Phones hive table representation 
 * 
 * @author azukovskij
 *
 */
@TableName("Phone")
public class CrmPhoneOutput extends ReportAggregate {

    private static final long serialVersionUID = -3082058616024529964L;

    private String phoneType;
    private String phoneNumber;
    private Date consentDate;
    private String consentStatus;
    
    public CrmPhoneOutput() {
    }
    
    public CrmPhoneOutput(CustomerReportOutput root, CrmPhone phone) {
        this.timestamp = root.getTimestamp();
        this.rootId = root.getRootId();
        this.revisionNo = root.getRevisionNo();
        this.parentId = root.getRootId();
        this.id = String.valueOf(phone.getKey().getId());

        this.phoneType = phone.getType();
        this.phoneNumber = phone.getValue();
        
        ConsentInfo consentInfo = phone.getConsentInfo();
        if(consentInfo != null) {
            this.consentDate = toSqlDate(consentInfo.getConsentDate());
            this.consentStatus = consentInfo.getConsentStatus();
        }
    }
    
    public String getPhoneType() {
        return phoneType;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public String getConsentStatus() {
        return consentStatus;
    }

    public Date getConsentDate() {
        return consentDate;
    }

    public void setConsentDate(Date consentDate) {
        this.consentDate = consentDate;
    }
    
    
}