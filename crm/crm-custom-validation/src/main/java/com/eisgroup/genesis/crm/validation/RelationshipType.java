/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.validation;

import com.eisgroup.genesis.factory.modeling.types.Customer;

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
 * Validate relationship type for applicable values
 * 
 * @author avoitau
 * 
 */
@Documented
@Constraint(validatedBy = { RelationshipTypeValidator.class })
@Target({METHOD, FIELD, TYPE})
@Retention(RUNTIME)
public @interface RelationshipType {

    String message() default "";

    Class<? extends Customer> type();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
