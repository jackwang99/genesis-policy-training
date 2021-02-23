/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.eis.dto;

/**
 * @author dlevchuk
 */
public class EISPerson extends DefaultParty {

    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String NATIONAL_ID = "nationalId";
    private static final String DECEASED = "deceased";
    private static final String GENDER = "gender";
    private static final String BIRTH_DATE = "birthDate";
    private static final String SALUTATION = "salutation";
    private static final String MARITAL_STATUS = "maritalStatus";
    private static final String SUFFIX = "suffix";
    private static final String CUSTOMER_NUMBER = "customerNumber";
    private static final String ARCHIVED = "archived";
    private static final String AGENCY = "agency";
    private static final String CONFIDENTIAL_FLAG = "confidentialFlag";
    private static final String CONFIDENTIAL_REFERENCE = "confidentialReference";

    public String getFirstName() {
        return getAttributes().get(FIRST_NAME);
    }

    public void setFirstName(String firstName) {
        getAttributes().put(FIRST_NAME, firstName);
    }

    public String getLastName() {
        return getAttributes().get(LAST_NAME);
    }

    public void setLastName(String lastName) {
        getAttributes().put(LAST_NAME, lastName);
    }

    public String getNationalId() {
        return getAttributes().get(NATIONAL_ID);
    }

    public void setNationalId(String nationalId) {
        getAttributes().put(NATIONAL_ID, nationalId);
    }

    public String getDeceased() {
        return getAttributes().get(DECEASED);
    }

    public void setDeceased(String deceased) {
        getAttributes().put(DECEASED, deceased);
    }

    public String getGender() {
        return getAttributes().get(GENDER);
    }

    public void setGender(String gender) {
        getAttributes().put(GENDER, gender);
    }

    public String getBirthDate() {
        return getAttributes().get(BIRTH_DATE);
    }

    public void setBirthDate(String birthDate) {
        getAttributes().put(BIRTH_DATE, birthDate);
    }

    public String getSalutation() {
        return getAttributes().get(SALUTATION);
    }

    public void setSalutation(String salutation) {
        getAttributes().put(SALUTATION, salutation);
    }

    public String getMaritalStatus() {
        return getAttributes().get(MARITAL_STATUS);
    }

    public void setMaritalStatus(String maritalStatus) {
        getAttributes().put(MARITAL_STATUS, maritalStatus);
    }

    public String getSuffix() {
        return getAttributes().get(SUFFIX);
    }

    public void setSuffix(String suffix) {
        getAttributes().put(SUFFIX, suffix);
    }

    public String getCustomerNumber() {
        return getAttributes().get(CUSTOMER_NUMBER);
    }

    public void setCustomerNumber(String customerNumber) {
        getAttributes().put(CUSTOMER_NUMBER, customerNumber);
    }

    public String getArchived() {
        return getAttributes().get(ARCHIVED);
    }

    public void setArchived(String archived) {
        getAttributes().put(ARCHIVED, archived);
    }

    public String getAgency() {
        return getAttributes().get(AGENCY);
    }

    public void setAgency(String agency) {
        getAttributes().put(AGENCY, agency);
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
