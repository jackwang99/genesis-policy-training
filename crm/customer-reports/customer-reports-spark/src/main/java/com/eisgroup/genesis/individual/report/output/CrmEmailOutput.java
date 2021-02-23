/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws. 
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.eisgroup.genesis.individual.report.output;

import static com.eisgroup.genesis.report.util.DateUtils.*;

import java.sql.Date;

import com.eisgroup.genesis.factory.modeling.types.immutable.ConsentInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.CrmEmail;
import com.eisgroup.genesis.report.ReportOutput.ReportAggregate;
import com.eisgroup.genesis.report.ReportOutput.TableName;

/**
 * Emails hive table representation 
 * 
 * @author azukovskij
 *
 */
@TableName("Email")
public class CrmEmailOutput extends ReportAggregate {

    private static final long serialVersionUID = -5227907472199766947L;
    
    private String emailType;
    private String emailAddress;
    private Date consentDate;
    private String consentStatus;
    
    public CrmEmailOutput() {
    }
    
    public CrmEmailOutput(CustomerReportOutput root, CrmEmail email) {
        this.timestamp = root.getTimestamp();
        this.rootId = root.getRootId();
        this.revisionNo = root.getRevisionNo();
        this.parentId = root.getRootId();
        this.id = String.valueOf(email.getKey().getId());

        this.emailType = email.getType();
        this.emailAddress = email.getValue();
        
        ConsentInfo consentInfo = email.getConsentInfo();
        if(consentInfo != null) {
            this.consentDate = toSqlDate(consentInfo.getConsentDate());
            this.consentStatus = consentInfo.getConsentStatus();
        }
    }
    
    public String getEmailType() {
        return emailType;
    }
    
    public String getEmailAddress() {
        return emailAddress;
    }
    
    public String getConsentStatus() {
        return consentStatus;
    }

    public Date getConsentDate() {
        return consentDate;
    }

}
