/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws. 
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.eisgroup.genesis.individual.report.input;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

import com.eisgroup.genesis.factory.model.individualcustomer.immutable.GenesisCrmCommunicationInfo;
import com.eisgroup.genesis.factory.model.individualcustomer.immutable.GenesisCrmEmploymentDetails;
import com.eisgroup.genesis.factory.model.individualcustomer.immutable.GenesisCrmIndividualDetails;
import com.eisgroup.genesis.factory.model.individualcustomer.immutable.IndividualCustomer;
import com.eisgroup.genesis.factory.modeling.types.immutable.AccessTrackInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.ProductOwned;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.report.IdentifiableRow;
import com.eisgroup.genesis.report.json.JsonArrayDelegate;
import com.eisgroup.genesis.report.json.JsonObjectDelegate;

/**
 * DTO for reading from ind. customer cassandra table
 * 
 * @author azukovskij
 *
 */
public class IndividualCustomerRow implements IdentifiableRow<IndividualCustomerRow> {

    private static final long serialVersionUID = 5200158684175621591L;

    static {
        JsonObjectDelegate.registerConverter(GenesisCrmIndividualDetails.class);
        JsonObjectDelegate.registerConverter(GenesisCrmCommunicationInfo.class);
        JsonArrayDelegate.registerConverter(GenesisCrmEmploymentDetails.class);
        JsonArrayDelegate.registerConverter(ProductOwned.class);
        JsonObjectDelegate.registerConverter(AccessTrackInfo.class);
    }
    
    public static final String[] COLUMN_NAMES = new String[] {
            "rootId", "revisionNo", "_timestamp", "state", "customerNumber", "brandCd", 
                "details", "communicationInfo", "productsOwned", "employmentDetails", "source", "accessTrackInfo"
    };

    private String rootId;
    private Timestamp timestamp;
    private Integer revisionNo;
    
    private String state;
    private String customerNumber;
    private String brandCd;
    private String source;
    private JsonObjectDelegate<GenesisCrmIndividualDetails> details;
    private JsonObjectDelegate<GenesisCrmCommunicationInfo> communicationInfo;
    private JsonArrayDelegate<GenesisCrmEmploymentDetails> employmentDetails;
    private JsonArrayDelegate<ProductOwned> productsOwned;
    private JsonObjectDelegate<AccessTrackInfo> accessTrackInfo;
    
    public IndividualCustomerRow() {
    }
    
    public IndividualCustomerRow(String rootId, Timestamp timestamp, Integer revisionNo, String state, String customerNumber,
            String brandCd, String source, JsonObjectDelegate<GenesisCrmIndividualDetails> details,
            JsonObjectDelegate<GenesisCrmCommunicationInfo> communicationInfo,
            JsonArrayDelegate<GenesisCrmEmploymentDetails> employmentDetails,
            JsonArrayDelegate<ProductOwned> productsOwned,
            JsonObjectDelegate<AccessTrackInfo> accessTrackInfo) {
        this.rootId = rootId;
        this.timestamp = timestamp;
        this.revisionNo = revisionNo;
        this.state = state;
        this.customerNumber = customerNumber;
        this.brandCd = brandCd;
        this.details = details;
        this.communicationInfo = communicationInfo;
        this.employmentDetails = employmentDetails;
        this.productsOwned = productsOwned;
        this.accessTrackInfo = accessTrackInfo;
        this.source = source;
    }

    public IndividualCustomerRow(IndividualCustomer customer) {
        RootEntityKey key = customer.getKey();
        this.rootId = String.valueOf(key.getRootId());
        this.revisionNo = key.getRevisionNo();
        this.timestamp = customer.getTimestamp()
                .map(Timestamp::valueOf)
                .orElse(null);

        this.state = customer.getState();
        this.customerNumber = customer.getCustomerNumber();
        this.brandCd = customer.getBrandCd();
        this.source = customer.getSource();
        this.details = new JsonObjectDelegate<>(GenesisCrmIndividualDetails.class, customer.getDetails());
        this.communicationInfo = new JsonObjectDelegate<>(GenesisCrmCommunicationInfo.class, customer.getCommunicationInfo());
        this.employmentDetails = new JsonArrayDelegate<>(GenesisCrmEmploymentDetails.class, customer.getEmploymentDetails());
        this.productsOwned = new JsonArrayDelegate<>(ProductOwned.class, customer.getProductsOwned());
        this.accessTrackInfo = new JsonObjectDelegate<>(AccessTrackInfo.class, customer.getAccessTrackInfo());
    }
    
    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getRevisionNo() {
        return revisionNo;
    }

    public void setRevisionNo(Integer revisionNo) {
        this.revisionNo = revisionNo;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getBrandCd() {
        return brandCd;
    }

    public String getSource() {
        return source;
    }

    public void setBrandCd(String brandCd) {
        this.brandCd = brandCd;
    }
    
    public JsonObjectDelegate<GenesisCrmIndividualDetails> getDetails() {
        return details;
    }
    
    public void setDetails(JsonObjectDelegate<GenesisCrmIndividualDetails> details) {
        this.details = details;
    }

    public JsonObjectDelegate<GenesisCrmCommunicationInfo> getCommunicationInfo() {
        return communicationInfo;
    }

    public JsonObjectDelegate<AccessTrackInfo> getAccessTrackInfo() {
        return accessTrackInfo;
    }


    public void setCommunicationInfo(JsonObjectDelegate<GenesisCrmCommunicationInfo> communicationInfo) {
        this.communicationInfo = communicationInfo;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public JsonArrayDelegate<GenesisCrmEmploymentDetails> getEmploymentDetails() {
        return employmentDetails;
    }

    public void setEmploymentDetails(JsonArrayDelegate<GenesisCrmEmploymentDetails> employmentDetails) {
        this.employmentDetails = employmentDetails;
    }

    public JsonArrayDelegate<ProductOwned> getProductsOwned() {
        return productsOwned;
    }

    public void setProductsOwned(JsonArrayDelegate<ProductOwned> productsOwned) {
        this.productsOwned = productsOwned;
    }

    @Override
    public int compareTo(IndividualCustomerRow other) {
        Comparator<IdentifiableRow<?>> comparing = Comparator.comparing(IdentifiableRow::getRevisionNo);
        return comparing.compare(this, other);
    }
    
}
