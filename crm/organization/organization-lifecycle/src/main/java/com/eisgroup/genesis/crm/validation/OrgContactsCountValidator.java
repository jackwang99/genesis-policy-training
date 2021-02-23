/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.eisgroup.genesis.factory.model.organizationcustomer.GenesisCrmCommunicationInfo;

/**
 * Check that the contacts count of a CommunicationInfo is between <i>min</i> and <i>max</i>
 *
 * @author avoitau
 */
public class OrgContactsCountValidator implements ConstraintValidator<OrgContactsCount, GenesisCrmCommunicationInfo> {
    private int min;
    private int max;

    @Override
    public void initialize(OrgContactsCount parameters) {
        min = parameters.min();
        max = parameters.max();
        validateParameters();
    }

    /**
     * Checks the number of contacts in an communication info object.
     *
     * @param value The communication info object to validate.
     * @param constraintValidatorContext context in which the constraint is evaluated.
     *
     * @return Returns <code>true</code> if the communication info is
     *         <code>null</code> or the number of contacts in
     *         <code>CommunicationInfo</code> is between the specified
     *         <code>min</code> and <code>max</code> values
     *         (inclusive), <code>false</code> otherwise.
     */
    @Override
    public boolean isValid(GenesisCrmCommunicationInfo value, ConstraintValidatorContext constraintValidatorContext) {
        if ( value == null ) {
            return true;
        }
        
        int length = 0;

        if (value.getAddresses() != null) {
            length += value.getAddresses().size();
        }

        if (value.getChats() != null) {
            length += value.getChats().size();
        }

        if (value.getEmails() != null) {
            length += value.getEmails().size();
        }

        if (value.getPhones() != null) {
            length += value.getPhones().size();
        }

        if (value.getSocialNets() != null) {
            length += value.getSocialNets().size();
        }

        if (value.getWebAddresses() != null) {
            length += value.getWebAddresses().size();
        }

        return length >= min && length <= max;
    }

    private void validateParameters() {
        if ( min < 0 ) {
            throw new IllegalArgumentException( "The min parameter cannot be negative." );
        }
        if ( max < 0 ) {
            throw new IllegalArgumentException( "The max parameter cannot be negative." );
        }
        if ( max < min ) {
            throw new IllegalArgumentException( "The length cannot be negative." );
        }
    }
}