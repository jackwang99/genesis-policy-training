/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.json.reducer.ref.model;

import com.eisgroup.genesis.json.reducer.ExecutionContext;
import com.eisgroup.genesis.product.specification.general.ProductModel;

/**
 * Execution Context with additional information.
 *
 * @author ssauchuk
 * @since 10.2
 */
public class TransformationExecutionContext extends ExecutionContext<ProductModel> {

    /**
     * Specified how {@link ProductModel} was filtered by behavior.
     * Used in attribute name transformation, see {@link AttributeTransformerDecisionTable}
     */
    private final String behaviourValue;

    /**
     * Constructs a execution context with product specification and
     *
     * @param productModel the product specification
     * @param behaviourValue       the behaviour value by which the product specification was filtered.
     */
    public TransformationExecutionContext(ProductModel productModel, String behaviourValue) {
        super(productModel);
        this.behaviourValue = behaviourValue;
    }

    /**
     * @return behaviour value by which the product specification filtered
     */
    public String getBehaviourValue() {
        return behaviourValue;
    }
}
