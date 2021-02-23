/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 
 * @author Dmitry Andronchik
 */
@Documented
@Target({ TYPE, METHOD, FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = { UniqueDimensionAgencyValidator.class })
public @interface UniqueDimensionAgency {

    String message() default "{crm.dimensionHolder.uniqueAgency}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
