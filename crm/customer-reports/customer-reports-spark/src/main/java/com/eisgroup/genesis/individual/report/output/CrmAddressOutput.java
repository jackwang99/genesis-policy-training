/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws. 
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.eisgroup.genesis.individual.report.output;

import com.eisgroup.genesis.factory.modeling.types.immutable.CrmAddress;
import com.eisgroup.genesis.factory.modeling.types.immutable.LocationBase;
import com.eisgroup.genesis.report.ReportOutput.ReportAggregate;
import com.eisgroup.genesis.report.ReportOutput.TableName;

/**
 * Customer address hive table representation
 * 
 * @author azukovskij
 *
 */
@TableName("Address")
public class CrmAddressOutput extends ReportAggregate {

    private static final long serialVersionUID = 7205279806482627728L;

    private Boolean preferred;
    private String countryCd;
    private String stateProvinceCd;
    private String city;
    private String postalCode;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    
    public CrmAddressOutput() {
    }
    
    public CrmAddressOutput(CustomerReportOutput root, CrmAddress address) {
        this.timestamp = root.getTimestamp();
        this.rootId = root.getRootId();
        this.revisionNo = root.getRevisionNo();
        this.parentId = root.getRootId();
        this.id = String.valueOf(address.getKey().getId());

        this.preferred = address.getPreferred();
        
        LocationBase location = address.getLocation();
        if(location != null) {
            this.countryCd = location.getCountryCd();
            this.stateProvinceCd = location.getStateProvinceCd();
            this.city = location.getCity();
            this.postalCode = location.getPostalCode();
            this.addressLine1 = location.getAddressLine1();
            this.addressLine2 = location.getAddressLine2();
            this.addressLine3 = location.getAddressLine3();
        }
    }

    public Boolean getPreferred() {
        return preferred;
    }

    public void setPreferred(Boolean preferred) {
        this.preferred = preferred;
    }

    public String getCountryCd() {
        return countryCd;
    }

    public void setCountryCd(String countryCd) {
        this.countryCd = countryCd;
    }

    public String getStateProvinceCd() {
        return stateProvinceCd;
    }

    public void setStateProvinceCd(String stateProvinceCd) {
        this.stateProvinceCd = stateProvinceCd;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

}
