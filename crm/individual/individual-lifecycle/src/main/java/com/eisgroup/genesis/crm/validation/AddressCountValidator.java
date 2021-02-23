/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.eisgroup.genesis.factory.model.individualcustomer.GenesisCrmCommunicationInfo;
import com.eisgroup.genesis.factory.model.individualcustomer.IndividualCustomer;

/**
 * At least one malling address for Organization
 *
 * @author avoitau
 */
public class AddressCountValidator implements ConstraintValidator<AddressCount, IndividualCustomer> {

    private static final String QUALIFIED_STATE = "qualified";

    @Override
    public void initialize(AddressCount parameters) {
        
    }

    @Override
    public boolean isValid(IndividualCustomer customer, ConstraintValidatorContext constraintValidatorContext) {
        if (customer == null || customer.getCommunicationInfo() == null) {
            return true;
        }
        
        if(StringUtils.isNotBlank(customer.getState()) 
                && !QUALIFIED_STATE.equalsIgnoreCase(customer.getState())){
            return true;
        }
        
        GenesisCrmCommunicationInfo communicationInfo = customer.getCommunicationInfo();
        return CollectionUtils.isNotEmpty(communicationInfo.getAddresses());
    }

}