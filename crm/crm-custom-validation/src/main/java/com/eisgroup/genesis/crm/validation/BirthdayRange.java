/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validate birthday field to be in provided range of years
 * 
 * @author avoitau
 *
 */
@Documented
@Constraint(validatedBy = { BirthdayRangeValidator.class })
@Target({ METHOD, FIELD})
@Retention(RUNTIME)
public @interface BirthdayRange {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum allowed age in years
     *
     * @return minimum allowed age in years
     */
    int min() default 0;

    /**
     * Maximum allowed age in years
     *
     * @return maximum allowed age in years
     */
    int max();
}
