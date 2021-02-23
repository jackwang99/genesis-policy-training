/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.time.LocalDate;


/**
 * Check that annotated birthday field is between <i>min</i> and <i>max</i> years. The verification
 * is made based on current system date.
 *
 * @author avoitau
 */
public class BirthdayRangeValidator implements ConstraintValidator<BirthdayRange, LocalDate> {
    
    private int min;
    private int max;
    
    @Override
    public void initialize(BirthdayRange parameters) {
        min = parameters.min();
        max = parameters.max();
        validateParameters();
    }

    /**
     * Checks that provided age is inside allowed range of years.
     *
     * @param birthday date to validate.
     * @param constraintValidatorContext context in which the constraint is evaluated.
     *
     * @return Returns <code>true</code> if provided age (according to birthday date) is
     *         inside allowed range of years, <code>false</code> otherwise.
     */
    @Override
    public boolean isValid(LocalDate birthday, ConstraintValidatorContext constraintValidatorContext) {
        if (birthday != null) {
            LocalDate today = LocalDate.now();
            return today.plusDays(1).isAfter(birthday.plusYears(min))
                    && today.isBefore(birthday.plusYears(max));
        }
        return true;
    }


    private void validateParameters() {
        if ( min < 0 ) {
            throw new IllegalArgumentException( "The min parameter cannot be negative." );
        }
        if ( max < 0 ) {
            throw new IllegalArgumentException( "The max parameter cannot be negative." );
        }
        if ( max < min ) {
            throw new IllegalArgumentException( "The range cannot be negative." );
        }
    }
}
