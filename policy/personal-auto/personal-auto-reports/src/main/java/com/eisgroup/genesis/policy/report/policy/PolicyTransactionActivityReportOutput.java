/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.policy.report.policy;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.eisgroup.genesis.factory.modeling.types.immutable.AccessTrackInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.PolicyBusinessDimensions;
import com.eisgroup.genesis.factory.modeling.types.immutable.TermDetails;
import com.eisgroup.genesis.factory.modeling.types.immutable.TransactionDetails;
import com.eisgroup.genesis.policy.report.AutoBlobRow;
import com.eisgroup.genesis.policy.report.LineOfBusinessOutput;
import com.eisgroup.genesis.policy.report.LineOfBusinessRow;
import com.eisgroup.genesis.policy.report.PolicySummaryRow;
import com.eisgroup.genesis.policy.report.PremiumEntryOutput;
import com.eisgroup.genesis.policy.report.PremiumsRow;
import com.eisgroup.genesis.policy.report.RiskItemRow;
import com.eisgroup.genesis.report.ReportOutput.ReportRoot;
import com.eisgroup.genesis.report.ReportOutput.TableName;
import com.eisgroup.genesis.report.ReportOutput.VersionedUsing;

/**
 * Hive structure for policy transaction activity report output (root table)
 *
 * @author mguzelis
 */
@TableName("Policy")
@VersionedUsing(PolicyVersioningStrategy.class)
public class PolicyTransactionActivityReportOutput extends ReportRoot {
    private static final long serialVersionUID = 5629364122868816623L;
    private static Long premiumEntryIndex = 0L;

    private String productCd;
    private String riskStateCd;
    private String policyNumber;
    private String state;
    private String txType;
    private Timestamp txCreateDate;
    private Timestamp txEffectiveDate;
    private String agency;
    private String subProducer;
    private Date termEffectiveDate;
    private Date termExpirationDate;
    private Timestamp updatedOn;
    private String updatedBy;
    private String blobCd;
    private String customerId;
    private String variation;

    private List<LineOfBusinessOutput> lobs = Collections.emptyList();
    private List<PremiumEntryOutput> premiumEntries = Collections.emptyList();

    public PolicyTransactionActivityReportOutput(PolicySummaryRow policy, Iterable<AutoBlobRow> blob, Iterable<PremiumsRow> premiums, Iterable<LineOfBusinessRow> lobs, Iterable<RiskItemRow> riskItems) {
        this(policy);
        StreamSupport.stream(blob.spliterator(), false)
                .findFirst()
                .ifPresent(autoBlobRow -> setBlobCd(autoBlobRow.getBlobCd()));

        this.premiumEntries = StreamSupport.stream(premiums.spliterator(), false)
                .flatMap(row -> row.getPremiumEntries().getEntityList().stream())
                .map(premiumEntry -> new PremiumEntryOutput(this, premiumEntry, premiumEntryIndex++))
                .collect(Collectors.toList());

        this.lobs = StreamSupport.stream(lobs.spliterator(), false)
                .map(lineOfBusiness -> {
                    LineOfBusinessOutput lobOutput = new LineOfBusinessOutput(this, lineOfBusiness);
                    setRiskItemCountForLob(lobOutput, riskItems);
                    return lobOutput;
                })
                .collect(Collectors.toList());
    }

    public PolicyTransactionActivityReportOutput(PolicySummaryRow policy) {
        this(policy.getRootId(), policy.getRevisionNo(), policy.getTimestamp());

        this.productCd = policy.getProductCd();
        this.riskStateCd = policy.getRiskStateCd();
        this.policyNumber = policy.getPolicyNumber();
        this.state = policy.getState();
        this.variation = policy.getVariation();
        this.customerId = parseIdFromLink(policy.getCustomer());

        TransactionDetails transactionDetails = policy.getTransactionDetails().getEntity();
        if (transactionDetails != null) {
            this.txType = transactionDetails.getTxType();
            this.txCreateDate = Timestamp.valueOf(transactionDetails.getTxCreateDate());
            this.txEffectiveDate = Timestamp.valueOf(transactionDetails.getTxEffectiveDate());
        }

        PolicyBusinessDimensions businessDimensions = policy.getBusinessDimensions().getEntity();
        if (businessDimensions != null) {
            this.agency = businessDimensions.getAgency();
            this.subProducer = businessDimensions.getSubProducer();
        }

        TermDetails termDetails = policy.getTermDetails().getEntity();
        if (termDetails != null) {
            this.termEffectiveDate = Date.valueOf(termDetails.getTermEffectiveDate());
            this.termExpirationDate = Date.valueOf(termDetails.getTermExpirationDate());
        }

        AccessTrackInfo accessTrackInfo = policy.getAccessTrackInfo().getEntity();
        if (accessTrackInfo != null) {
            this.updatedOn = Timestamp.valueOf(accessTrackInfo.getUpdatedOn());
            this.updatedBy = accessTrackInfo.getUpdatedBy();
        }
    }

    private PolicyTransactionActivityReportOutput(String rootId, Integer revisionNo, Timestamp timestamp) {
        this.rootId = rootId;
        this.revisionNo = revisionNo;
        this.timestamp = Optional.ofNullable(timestamp)
                .orElse(null);
    }

    private String parseIdFromLink(String pathToEntity) {
        String id = pathToEntity.substring(pathToEntity.lastIndexOf("//") + 2);
        if (id.contains("/")) {
            return id.substring(0, id.lastIndexOf('/'));
        }
        return id;
    }

    private void setRiskItemCountForLob(LineOfBusinessOutput lob, Iterable<RiskItemRow> riskItems) {
        Long riskItemCount = StreamSupport.stream(riskItems.spliterator(), false)
                .filter(riskItem -> riskItem.getParentId().equals(lob.getId()))
                .count();
        lob.setNumberOfRiskItems(riskItemCount);
    }

    @Override
    public <T extends ReportAggregate> List<T> getAggregates(Class<T> aggregateType) {
        if (LineOfBusinessOutput.class.equals(aggregateType)) {
            return (List<T>) lobs;
        }
        if (PremiumEntryOutput.class.equals(aggregateType)) {
            return (List<T>) premiumEntries;
        }
        throw new IllegalStateException("Unsupported aggregate type " + aggregateType);
    }

    public String getProductCd() {
        return productCd;
    }

    public String getRiskStateCd() {
        return riskStateCd;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public String getState() {
        return state;
    }

    public String getTxType() {
        return txType;
    }

    public Timestamp getTxCreateDate() {
        return txCreateDate;
    }

    public Timestamp getTxEffectiveDate() {
        return txEffectiveDate;
    }

    public String getAgency() {
        return agency;
    }

    public String getSubProducer() {
        return subProducer;
    }

    public Date getTermEffectiveDate() {
        return termEffectiveDate;
    }

    public Date getTermExpirationDate() {
        return termExpirationDate;
    }

    public Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getBlobCd() {
        return blobCd;
    }

    public String getVariation() {
        return variation;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setBlobCd(String blobCd) {
        this.blobCd = blobCd;
    }

}
