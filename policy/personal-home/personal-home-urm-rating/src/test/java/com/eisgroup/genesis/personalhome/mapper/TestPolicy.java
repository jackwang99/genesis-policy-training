/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.personalhome.mapper;

import com.eisgroup.genesis.factory.model.personalhome.PersonalHomeAddressInfo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhchen
 * @since 1.0
 */
public class TestPolicy {
    private TermDetails termDetails;
    private RiskItemslocation[] riskItems;

    public TermDetails getTermDetails() {
        return termDetails;
    }

    public void setTermDetails(TermDetails termDetails) {
        this.termDetails = termDetails;
    }

    public RiskItemslocation[] getRiskItems() {
        return riskItems;
    }

    public void setRiskItems(RiskItemslocation[] riskItem) {
        this.riskItems = riskItems;
    }

    public static class TermDetails {
        private Date termEffectiveDate;

        public Date getTermEffectiveDate() {
            return termEffectiveDate;
        }

        public void setTermEffectiveDate(Date termEffectiveDate) {
            this.termEffectiveDate = termEffectiveDate;
        }
    }

    public static class RiskItemslocation {

        private PersonalHomeAddressInfo address;
        private Variation[] variations;

        public PersonalHomeAddressInfo getAddress() {
            return address;
        }

        public void setAddress(PersonalHomeAddressInfo address) {
            this.address = address;
        }

        public Variation[] getVariations() {
            return variations;
        }

        public void setVariations(Variation[] variations) {
            this.variations = variations;
        }
    }

    public static class Address {
        private String postalCode;

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }
    }

    public static class Variation {
        private Coverage[] coverages;

        public Coverage[] getCoverages() {
            return coverages;
        }

        public void setCoverages(Coverage[] coverages) {
            this.coverages = coverages;
        }
    }

    public static class Coverage {
        private BigDecimal deductibleAmount;
        private BigDecimal limitAmount;
        private String code;

        public BigDecimal getDeductibleAmount() {
            return deductibleAmount;
        }

        public void setDeductibleAmount(BigDecimal deductibleAmount) {
            this.deductibleAmount = deductibleAmount;
        }

        public BigDecimal getLimitAmount() {
            return limitAmount;
        }

        public void setLimitAmount(BigDecimal limitAmount) {
            this.limitAmount = limitAmount;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

    }
}
