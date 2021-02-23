/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.security;

/**
 * Holds Product Server privileges.
 *
 *
 * @author ssauchuk
 * @since 10.11
 */
public final class Privileges {

    public static final String EXPORT_PRODUCT = "Product Server: Export product";
    public static final String CONVERT_PRODUCT = "Product Server: Convert product";

    public static final String PIPELINE_EXECUTION = "Product Server: Pipeline execution";

    private Privileges() {
        throw new IllegalStateException("Cannot instantiate a utility class");
    }
}
