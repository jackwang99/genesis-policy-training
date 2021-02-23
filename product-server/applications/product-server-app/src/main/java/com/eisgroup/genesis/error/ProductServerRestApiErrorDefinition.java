/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.error;

import com.eisgroup.genesis.exception.BaseErrorDefinition;

/**
 * Rest API errors definition.
 *
 * @author ssauchuk
 * @since 10.5
 */
public class ProductServerRestApiErrorDefinition extends BaseErrorDefinition {

    /**
     * Is shown when operation could not be performed as target policy specification not be loaded
     */
    public static final ProductServerRestApiErrorDefinition PRODUCT_SPECIFICATION_NOT_FOUND = new ProductServerRestApiErrorDefinition("rps0001",
            "Operation could not be performed as product specification could not be loaded");

    public static final ProductServerRestApiErrorDefinition EXPORT_TO_SPECIFIED_FORMAT_IS_NOT_AVAILABLE =
            new ProductServerRestApiErrorDefinition("rps0002", "Export to {0} format is not available");

    public static final ProductServerRestApiErrorDefinition CONVERTING_SPECIFICATION_IS_NOT_AVAILABLE =
            new ProductServerRestApiErrorDefinition("rps0003", "Converting specification from {0} format "
                    + "to {1} format is not available");

    protected ProductServerRestApiErrorDefinition(String code, String message) {
        super(code, message);
    }
}
