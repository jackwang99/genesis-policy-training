/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.personalauto.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author dl on 8/2/17.
 */
public class TestPolicy {
    private TermDetails termDetails;
    private Vehicle[] riskItems;

    public PremiumHolder[] getPolicyPremiums() {
        return policyPremiums;
    }

    public void setPolicyPremiums(PremiumHolder[] policyPremiums) {
        this.policyPremiums = policyPremiums;
    }

    private PremiumHolder[] policyPremiums;

    public TermDetails getTermDetails() {
        return termDetails;
    }

    public void setTermDetails(TermDetails termDetails) {
        this.termDetails = termDetails;
    }

    public Vehicle[] getRiskItems() {
        return riskItems;
    }

    public void setRiskItems(Vehicle[] riskItems) {
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

    public static class Vehicle {
        private String collSymbol;
        private String antiLockBrakeCd;
        private String liabSymbol;
        private String biSymbol;
        private String model;
        private String usageCd;
        private Long annualMiles;
        private Boolean recoveryDeviceInd;
        private Boolean armoredInd;
        private String compSymbol;
        private Long modelYear;
        private String pipMedSymbol;
        private String securityOptionsCd;
        private Boolean daytimeRunningLampsInd;
        private BigDecimal adjustedValue;
        private String airBagStatusCd;
        private String pdSymbol;
        private String typeCd;
        private Boolean automaticBeltsInd;

        private Address address;
        private Variation[] variations;

        public String getCollSymbol() {
            return collSymbol;
        }

        public void setCollSymbol(String collSymbol) {
            this.collSymbol = collSymbol;
        }

        public String getAntiLockBrakeCd() {
            return antiLockBrakeCd;
        }

        public void setAntiLockBrakeCd(String antiLockBrakeCd) {
            this.antiLockBrakeCd = antiLockBrakeCd;
        }

        public String getLiabSymbol() {
            return liabSymbol;
        }

        public void setLiabSymbol(String liabSymbol) {
            this.liabSymbol = liabSymbol;
        }

        public String getBiSymbol() {
            return biSymbol;
        }

        public void setBiSymbol(String biSymbol) {
            this.biSymbol = biSymbol;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getUsageCd() {
            return usageCd;
        }

        public void setUsageCd(String usageCd) {
            this.usageCd = usageCd;
        }

        public Long getAnnualMiles() {
            return annualMiles;
        }

        public void setAnnualMiles(Long annualMiles) {
            this.annualMiles = annualMiles;
        }

        public Boolean getRecoveryDeviceInd() {
            return recoveryDeviceInd;
        }

        public void setRecoveryDeviceInd(Boolean recoveryDeviceInd) {
            this.recoveryDeviceInd = recoveryDeviceInd;
        }

        public Boolean getArmoredInd() {
            return armoredInd;
        }

        public void setArmoredInd(Boolean armoredInd) {
            this.armoredInd = armoredInd;
        }

        public String getCompSymbol() {
            return compSymbol;
        }

        public void setCompSymbol(String compSymbol) {
            this.compSymbol = compSymbol;
        }

        public Long getModelYear() {
            return modelYear;
        }

        public void setModelYear(Long modelYear) {
            this.modelYear = modelYear;
        }

        public String getPipMedSymbol() {
            return pipMedSymbol;
        }

        public void setPipMedSymbol(String pipMedSymbol) {
            this.pipMedSymbol = pipMedSymbol;
        }

        public String getSecurityOptionsCd() {
            return securityOptionsCd;
        }

        public void setSecurityOptionsCd(String securityOptionsCd) {
            this.securityOptionsCd = securityOptionsCd;
        }

        public Boolean getDaytimeRunningLampsInd() {
            return daytimeRunningLampsInd;
        }

        public void setDaytimeRunningLampsInd(Boolean daytimeRunningLampsInd) {
            this.daytimeRunningLampsInd = daytimeRunningLampsInd;
        }

        public BigDecimal getAdjustedValue() {
            return adjustedValue;
        }

        public void setAdjustedValue(BigDecimal adjustedValue) {
            this.adjustedValue = adjustedValue;
        }

        public String getAirBagStatusCd() {
            return airBagStatusCd;
        }

        public void setAirBagStatusCd(String airBagStatusCd) {
            this.airBagStatusCd = airBagStatusCd;
        }

        public String getPdSymbol() {
            return pdSymbol;
        }

        public void setPdSymbol(String pdSymbol) {
            this.pdSymbol = pdSymbol;
        }

        public String getTypeCd() {
            return typeCd;
        }

        public void setTypeCd(String typeCd) {
            this.typeCd = typeCd;
        }

        public Boolean getAutomaticBeltsInd() {
            return automaticBeltsInd;
        }

        public void setAutomaticBeltsInd(Boolean automaticBeltsInd) {
            this.automaticBeltsInd = automaticBeltsInd;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
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

    public static class PremiumHolder {
        private String countryCode;
        private String stateCode;
        private String premiumCode;
        private String id;
        private Double premium;

        public PremiumHolder() {
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getStateCode() {
            return stateCode;
        }

        public void setStateCode(String stateCode) {
            this.stateCode = stateCode;
        }

        public String getPremiumCode() {
            return premiumCode;
        }

        public void setPremiumCode(String premiumCode) {
            this.premiumCode = premiumCode;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Double getPremium() {
            return premium;
        }

        public void setPremium(Double premium) {
            this.premium = premium;
        }
    }


    public static class Coverage {
        private BigDecimal deductibleAmount;
        private BigDecimal limitAmount;
        private String code;
        private LocalDate effectiveDate;

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

        public LocalDate getEffectiveDate() {
            return effectiveDate;
        }

        public void setEffectiveDate(LocalDate effectiveDate) {
            this.effectiveDate = effectiveDate;
        }
    }
}
