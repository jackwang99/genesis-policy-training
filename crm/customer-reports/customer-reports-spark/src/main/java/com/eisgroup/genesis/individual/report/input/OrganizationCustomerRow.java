/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws. 
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.eisgroup.genesis.individual.report.input;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

import com.eisgroup.genesis.factory.model.organizationcustomer.immutable.GenesisCrmBusinessDetails;
import com.eisgroup.genesis.factory.model.organizationcustomer.immutable.GenesisCrmCommunicationInfo;
import com.eisgroup.genesis.factory.model.organizationcustomer.immutable.OrganizationCustomer;
import com.eisgroup.genesis.factory.modeling.types.immutable.AccessTrackInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.ProductOwned;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.report.IdentifiableRow;
import com.eisgroup.genesis.report.json.JsonArrayDelegate;
import com.eisgroup.genesis.report.json.JsonObjectDelegate;

/**
 * DTO for reading from org. customer cassandra table
 * 
 * @author azukovskij
 *
 */
public class OrganizationCustomerRow implements IdentifiableRow<OrganizationCustomerRow> {

    private static final long serialVersionUID = 5313292157729306385L;
    
    public static final String[] COLUMN_NAMES = new String[] {
            "rootId", "revisionNo", "_timestamp", "state", "customerNumber", "brandCd", "source",
                "details", "communicationInfo", "productsOwned", "accessTrackInfo"
    };

    static {
        JsonObjectDelegate.registerConverter(GenesisCrmBusinessDetails.class);
        JsonObjectDelegate.registerConverter(GenesisCrmCommunicationInfo.class);
        JsonArrayDelegate.registerConverter(ProductOwned.class);
        JsonObjectDelegate.registerConverter(AccessTrackInfo.class);
    }

    private String rootId;
    private Timestamp timestamp;
    private Integer revisionNo;
    
    private String state;
    private String customerNumber;
    private String brandCd;
    private String source;
    private JsonObjectDelegate<GenesisCrmBusinessDetails> details;
    private JsonObjectDelegate<GenesisCrmCommunicationInfo> communicationInfo;
    private JsonArrayDelegate<ProductOwned> productsOwned;
    private JsonObjectDelegate<AccessTrackInfo> accessTrackInfo;
    
    public OrganizationCustomerRow() {
    }
    
    public OrganizationCustomerRow(String rootId, Timestamp timestamp, Integer revisionNo, String state,
            String customerNumber, String brandCd, String source, JsonObjectDelegate<GenesisCrmBusinessDetails> details,
            JsonObjectDelegate<GenesisCrmCommunicationInfo> communicationInfo,
            JsonArrayDelegate<ProductOwned> productsOwned, JsonObjectDelegate<AccessTrackInfo> accessTrackInfo) {
        this.rootId = rootId;
        this.timestamp = timestamp;
        this.revisionNo = revisionNo;
        this.state = state;
        this.customerNumber = customerNumber;
        this.brandCd = brandCd;
        this.source = source;
        this.details = details;
        this.communicationInfo = communicationInfo;
        this.productsOwned = productsOwned;
        this.accessTrackInfo = accessTrackInfo;
    }

    public OrganizationCustomerRow(OrganizationCustomer customer) {
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
        this.details = new JsonObjectDelegate<>(GenesisCrmBusinessDetails.class, customer.getDetails());
        this.communicationInfo = new JsonObjectDelegate<>(GenesisCrmCommunicationInfo.class, customer.getCommunicationInfo());
        this.productsOwned = new JsonArrayDelegate<>(ProductOwned.class, customer.getProductsOwned());
        this.accessTrackInfo = new JsonObjectDelegate<>(AccessTrackInfo.class, customer.getAccessTrackInfo());
    }
    
    public String getRootId() {
        return rootId;
    }
    
    public void setRootId(String rootId) {
        this.rootId = rootId;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    
    public Integer getRevisionNo() {
        return revisionNo;
    }
    
    public void setRevisionNo(Integer revisionNo) {
        this.revisionNo = revisionNo;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getCustomerNumber() {
        return customerNumber;
    }
    
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }
    
    public String getBrandCd() {
        return brandCd;
    }

    public String getSource() {
        return source;
    }

    public JsonObjectDelegate<AccessTrackInfo> getAccessTrackInfo() {
        return accessTrackInfo;
    }

    public void setBrandCd(String brandCd) {
        this.brandCd = brandCd;
    }
    
    public JsonObjectDelegate<GenesisCrmBusinessDetails> getDetails() {
        return details;
    }
    
    public void setDetails(JsonObjectDelegate<GenesisCrmBusinessDetails> details) {
        this.details = details;
    }
    
    public JsonObjectDelegate<GenesisCrmCommunicationInfo> getCommunicationInfo() {
        return communicationInfo;
    }
    
    public void setCommunicationInfo(JsonObjectDelegate<GenesisCrmCommunicationInfo> communicationInfo) {
        this.communicationInfo = communicationInfo;
    }
    
    public JsonArrayDelegate<ProductOwned> getProductsOwned() {
        return productsOwned;
    }
    
    public void setProductsOwned(JsonArrayDelegate<ProductOwned> productsOwned) {
        this.productsOwned = productsOwned;
    }

    @Override
    public int compareTo(OrganizationCustomerRow other) {
        Comparator<IdentifiableRow<?>> comparing = Comparator.comparing(IdentifiableRow::getRevisionNo);
        return comparing.compare(this, other);
    }
    
}
