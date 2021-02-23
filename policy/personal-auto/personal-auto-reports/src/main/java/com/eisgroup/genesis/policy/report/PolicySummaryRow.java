/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.report;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

import com.eisgroup.genesis.factory.model.personalauto.PersonalAutoPolicySummary;
import com.eisgroup.genesis.factory.modeling.types.immutable.AccessTrackInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.PolicyBusinessDimensions;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;
import com.eisgroup.genesis.factory.modeling.types.immutable.TermDetails;
import com.eisgroup.genesis.factory.modeling.types.immutable.TransactionDetails;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.json.link.EntityLink;
import com.eisgroup.genesis.report.json.JsonObjectDelegate;

/**
 * DTO for reading from policy summary cassandra table
 */
public class PolicySummaryRow implements Serializable {
    private static final long serialVersionUID = -5839553728811857675L;

    @SuppressWarnings("squid:S2386") // GENESIS-14408
    public static final String[] COLUMN_NAMES = new String[]{
            "rootId", "revisionNo", "policyNumber", "state", "accessTrackInfo", "transactionDetails", "customer", "variation"
    };

    static {
        JsonObjectDelegate.registerConverter(AccessTrackInfo.class);
        JsonObjectDelegate.registerConverter(TransactionDetails.class);
        JsonObjectDelegate.registerConverter(PolicyBusinessDimensions.class);
        JsonObjectDelegate.registerConverter(TermDetails.class);
        JsonObjectDelegate.registerConverter(EntityLink.class);
        JsonObjectDelegate.registerConverter(RootEntity.class);

    }

    private String rootId;
    private Integer revisionNo;
    private String policyNumber;
    private String state;
    private Timestamp timestamp;
    private String productCd;
    private String riskStateCd;
    private String variation;
    private JsonObjectDelegate<AccessTrackInfo> accessTrackInfo;
    private JsonObjectDelegate<TransactionDetails> transactionDetails;
    private JsonObjectDelegate<PolicyBusinessDimensions> businessDimensions;
    private JsonObjectDelegate<TermDetails> termDetails;
    private String customer;


    public PolicySummaryRow() {
    }

    public PolicySummaryRow(String rootId, Integer revisionNo, String policyNumber, String state, Timestamp timestamp,
                            String productCd, String riskStateCd, String _variation, JsonObjectDelegate<RootEntity> customer, JsonObjectDelegate<AccessTrackInfo> accessTrackInfo,
                            JsonObjectDelegate<TransactionDetails> transactionDetails,
                            JsonObjectDelegate<PolicyBusinessDimensions> businessDimensions,
                            JsonObjectDelegate<TermDetails> termDetails) {
        this.rootId = rootId;
        this.revisionNo = revisionNo;
        this.policyNumber = policyNumber;
        this.state = state;
        this.timestamp = timestamp;
        this.productCd = productCd;
        this.riskStateCd = riskStateCd;
        this.accessTrackInfo = accessTrackInfo;
        this.transactionDetails = transactionDetails;
        this.businessDimensions = businessDimensions;
        this.termDetails = termDetails;
        this.variation = _variation;
        this.customer = customer.getEntity().toString();
    }

    public PolicySummaryRow(PersonalAutoPolicySummary policy) {
        RootEntityKey key = policy.getKey();
        this.rootId = String.valueOf(key.getRootId());
        this.revisionNo = key.getRevisionNo();
        this.policyNumber = policy.getPolicyNumber();
        this.state = policy.getState();
        this.timestamp = policy.getTimestamp()
                .map(Timestamp::valueOf)
                .orElse(null);
        this.productCd = policy.getProductCd();
        this.riskStateCd = policy.getRiskStateCd();
        this.accessTrackInfo = new JsonObjectDelegate<>(AccessTrackInfo.class, policy.getAccessTrackInfo());
        this.transactionDetails = new JsonObjectDelegate<>(TransactionDetails.class, policy.getTransactionDetails());
        this.businessDimensions = new JsonObjectDelegate<>(PolicyBusinessDimensions.class, policy.getBusinessDimensions());
        this.termDetails = new JsonObjectDelegate<>(TermDetails.class, policy.getTermDetails());
        this.customer = policy.getCustomer().getURIString();
        this.variation = policy.getVariation().toString();
    }

    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    public Integer getRevisionNo() {
        return revisionNo;
    }

    public void setRevisionNo(Integer revisionNo) {
        this.revisionNo = revisionNo;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getProductCd() {
        return productCd;
    }

    public void setProductCd(String productCd) {
        this.productCd = productCd;
    }

    public String getRiskStateCd() {
        return riskStateCd;
    }

    public void setRiskStateCd(String riskStateCd) {
        this.riskStateCd = riskStateCd;
    }

    public JsonObjectDelegate<AccessTrackInfo> getAccessTrackInfo() {
        return accessTrackInfo;
    }

    public void setAccessTrackInfo(JsonObjectDelegate<AccessTrackInfo> accessTrackInfo) {
        this.accessTrackInfo = accessTrackInfo;
    }

    public JsonObjectDelegate<TransactionDetails> getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(JsonObjectDelegate<TransactionDetails> transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    public JsonObjectDelegate<PolicyBusinessDimensions> getBusinessDimensions() {
        return businessDimensions;
    }

    public void setBusinessDimensions(JsonObjectDelegate<PolicyBusinessDimensions> businessDimensions) {
        this.businessDimensions = businessDimensions;
    }

    public JsonObjectDelegate<TermDetails> getTermDetails() {
        return termDetails;
    }

    public void setTermDetails(JsonObjectDelegate<TermDetails> termDetails) {
        this.termDetails = termDetails;
    }

    public String getCustomer() {
        return this.customer;
    }

    public String getVariation() {
        return this.variation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PolicySummaryRow that = (PolicySummaryRow) o;
        return rootId.equals(that.rootId) &&
                revisionNo.equals(that.revisionNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rootId, revisionNo);
    }

}
