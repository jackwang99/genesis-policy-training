/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.eis.dto;

/**
 * @author dlevchuk
 */
public class EISNonPerson extends DefaultParty {

    private static final String AGENCY = "agency";
    private static final String BUSINESS_TYPE = "businessType";
    private static final String BUSINESS_NAME = "businessName";
    private static final String LEGAL_NAME = "legalName";
    private static final String LEGAL_ID = "legalId";
    private static final String DATE_STARTED = "dateStarted";
    private static final String SIC_CODE = "sicCode";
    private static final String CUSTOMER_NUMBER = "custoemrNumber";
    private static final String ARCHIEVED = "archieved";
    private static final String CONFIDENTIAL_FLAG = "confidentialFlag";
    private static final String CONFIDENTIAL_REFERENCE = "confidentialReference";

    public String getAgency() {
        return getAttributes().get(AGENCY);
    }

    public void setAgency(String agency) {
        getAttributes().put(AGENCY, agency);
    }

    public String getBusinessType() {
        return getAttributes().get(BUSINESS_TYPE);
    }

    public void setBusinessType(String businessType) {
        getAttributes().put(BUSINESS_TYPE, businessType);
    }

    public String getBusinessName() {
        return getAttributes().get(BUSINESS_NAME);
    }

    public void setBusinessName(String businessName) {
        getAttributes().put(BUSINESS_NAME, businessName);
    }

    public String getLegalName() {
        return getAttributes().get(LEGAL_NAME);
    }

    public void setLegalName(String legalName) {
        getAttributes().put(LEGAL_NAME, legalName);
    }

    public String getLegalId() {
        return getAttributes().get(LEGAL_ID);
    }

    public void setLegalId(String legalId) {
        getAttributes().put(LEGAL_ID, legalId);
    }

    public String getDateStarted() {
        return getAttributes().get(DATE_STARTED);
    }

    public void setDateStarted(String dateStarted) {
        getAttributes().put(DATE_STARTED, dateStarted);
    }

    public String getSicCode() {
        return getAttributes().get(SIC_CODE);
    }

    public void setSicCode(String sicCode) {
        getAttributes().put(SIC_CODE, sicCode);
    }

    public String getCustomerNumber() {
        return getAttributes().get(CUSTOMER_NUMBER);
    }

    public void setCustomerNumber(String customerNumber) {
        getAttributes().put(CUSTOMER_NUMBER, customerNumber);
    }

    public String getArchieved() {
        return getAttributes().get(ARCHIEVED);
    }

    public void setArchieved(String archieved) {
        getAttributes().put(ARCHIEVED, archieved);
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
