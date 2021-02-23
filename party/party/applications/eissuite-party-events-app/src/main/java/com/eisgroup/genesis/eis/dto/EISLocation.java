/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.eis.dto;

/**
 * @author dlevchuk
 */
public class EISLocation extends DefaultParty {

    private static final String STREET1 = "street1";
    private static final String CITY = "city";
    private static final String ZIP = "zip";
    private static final String STATE = "state";
    private static final String COUNTRY = "country";
    private static final String ADDRESS_TYPE_CD = "addressTypeCd";
    private static final String DO_NOT_SOLICIT = "doNotSolicit";
    private static final String CONFIDENTIAL_FLAG = "confidentialFlag";
    private static final String CONFIDENTIAL_REFERENCE = "confidentialReference";

    public String getStreet1() {
        return getAttributes().get(STREET1);
    }

    public void setStreet1(String street1) {
        getAttributes().put(STREET1, street1);
    }

    public String getCity() {
        return getAttributes().get(CITY);
    }

    public void setCity(String city) {
        getAttributes().put(CITY, city);
    }

    public String getState() {
        return getAttributes().get(STATE);
    }

    public void setState(String state) {
        getAttributes().put(STATE, state);
    }

    public String getZip() {
        return getAttributes().get(ZIP);
    }

    public void setZip(String zip) {
        getAttributes().put(ZIP, zip);
    }

    public String getCountry() {
        return getAttributes().get(COUNTRY);
    }

    public void setCountry(String country) {
        getAttributes().put(COUNTRY, country);
    }

    public String getAddressTypeCd() {
        return getAttributes().get(ADDRESS_TYPE_CD);
    }

    public void setAddressTypeCd(String addressTypeCd) {
        getAttributes().put(ADDRESS_TYPE_CD, addressTypeCd);
    }

    public String getDoNotSolicit() {
        return getAttributes().get(DO_NOT_SOLICIT);
    }

    public void setDoNotSolicit(String doNotSolicit) {
        getAttributes().put(DO_NOT_SOLICIT, doNotSolicit);
    }

    public String getConfidentialFlag() {
        return getAttributes().get(CONFIDENTIAL_FLAG);
    }

    public void setConfidentialFlag(String confidentialFlag) {
        getAttributes().put(CONFIDENTIAL_FLAG, confidentialFlag);
    }

    public String getConfidentialReference() {
        return getAttributes().get(CONFIDENTIAL_REFERENCE);
    }

    public void setConfidentialReference(String confidentialReference) {
        getAttributes().put(CONFIDENTIAL_REFERENCE, confidentialReference);
    }
}
