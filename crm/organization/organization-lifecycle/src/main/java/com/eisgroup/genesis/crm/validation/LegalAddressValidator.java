/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.eisgroup.genesis.factory.modeling.types.Location;
import org.apache.commons.collections4.CollectionUtils;

import com.eisgroup.genesis.factory.model.organizationcustomer.GenesisCrmCommunicationInfo;
import com.eisgroup.genesis.factory.model.organizationcustomer.OrganizationCustomer;

/**
 * At least one legal address for OrganizationCustomer
 *
 * @author avoitau
 */
public class LegalAddressValidator implements ConstraintValidator<LegalAddress, OrganizationCustomer> {

    public static final String LEGAL_ADDRESS_TYPE = "Legal";
    private static final String QUALIFIED_STATE = "qualified";
    
    @Override
    public void initialize(LegalAddress parameters) {
        
    }

    @Override
    public boolean isValid(OrganizationCustomer organization, ConstraintValidatorContext constraintValidatorContext) {
        if ( organization == null || organization.getCommunicationInfo() == null) {
            return true;
        }
        
        if(organization.getState() != null 
                && !QUALIFIED_STATE.equalsIgnoreCase(organization.getState())){
            return true;
        }
        
        GenesisCrmCommunicationInfo communicationInfo = organization.getCommunicationInfo();
        if(CollectionUtils.isEmpty(communicationInfo.getAddresses())) {
            return false;
        }
        
        return communicationInfo.getAddresses().stream().anyMatch(address -> {
           return address.getLocation() != null && LEGAL_ADDRESS_TYPE.equalsIgnoreCase(((Location) address.getLocation()).getAddressType());
        });
        
    }

}