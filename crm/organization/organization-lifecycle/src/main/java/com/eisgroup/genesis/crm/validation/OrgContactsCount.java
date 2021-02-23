/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated CommunicationInfo element contacts count must be between the specified boundaries (included).
 *
 * @author avoitau
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {OrgContactsCountValidator.class})
public @interface OrgContactsCount {
    String message() default "{crm.contactsCount}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum allowed number of contacts.
     *
     * @return minimum allowed number of contacts
     */
    int min() default 0;

    /**
     * Maximum allowed number of contacts.
     *
     * @return maximum allowed number of contacts
     */
    int max() default Integer.MAX_VALUE;

    /**
     * Defines several <code>@Size</code> annotations on the same element
     * @see ContactsCount
     *
     */
    @Target({ METHOD, FIELD })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        OrgContactsCount[] value();
    }
}