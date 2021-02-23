/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.report.quote;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.stream.StreamSupport;

import com.eisgroup.genesis.factory.modeling.types.immutable.AccessTrackInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.TransactionDetails;
import com.eisgroup.genesis.policy.report.PolicySummaryRow;
import com.eisgroup.genesis.policy.report.PremiumsRow;
import com.eisgroup.genesis.report.ReportOutput;
import com.eisgroup.genesis.report.ReportOutput.ReportRoot;
import com.eisgroup.genesis.report.ReportOutput.TableName;

/**
 * Quote premium calculation report output pojo.
 * Used to define {@link ReportOutput} report schema in data warehouse
 * 
 * @author azukovskij
 *
 */
@TableName("Quote")
public class QuotePremiumsReportOutput extends ReportRoot {
    
    private static final long serialVersionUID = 344935243350528659L;
    
    private String quoteNumber;
   
    private String quoteStatus;
    
    private Date quoteInitiationDate;
    
    private String quoteTransactionType;
    
    private Double termPremium;
    
    public QuotePremiumsReportOutput() {
    }
    
    public QuotePremiumsReportOutput(PolicySummaryRow quote, Iterable<PremiumsRow> premiums) {
        AccessTrackInfo accessTrackInfo = quote.getAccessTrackInfo().getEntity();
        TransactionDetails transactionDetails = quote.getTransactionDetails().getEntity();
        rootId = quote.getRootId();
        revisionNo = quote.getRevisionNo();
        quoteNumber = quote.getPolicyNumber();
        quoteStatus = quote.getState();
        quoteInitiationDate = Date.valueOf(accessTrackInfo.getCreatedOn().toLocalDate());
        quoteTransactionType = transactionDetails.getTxType();
        termPremium = StreamSupport.stream(premiums.spliterator(), false)
            .flatMap(row -> row.getPremiumEntries().getEntityList().stream())
            .filter(entry -> "GWP".equals(entry.getPremiumCode()))
            .map(entry -> entry.getTermAmount().getNumberStripped())
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .doubleValue();
        
        timestamp = Timestamp.valueOf((accessTrackInfo.getUpdatedOn() == null ? 
                accessTrackInfo.getCreatedOn() : 
                    accessTrackInfo.getUpdatedOn()));
    }

    public String getQuoteNumber() {
        return quoteNumber;
    }
    
    public String getQuoteStatus() {
        return quoteStatus;
    }
    
    public Date getQuoteInitiationDate() {
        return quoteInitiationDate;
    }
    
    public String getQuoteTransactionType() {
        return quoteTransactionType;
    }
    
    public Double getTermPremium() {
        return termPremium;
    }

}
