/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.design.tooling.facade.dto;

/**
 * Product filter, for filtering the products by additional meta-info
 *
 * @author dlevchuk
 * @since 10.10
 */
public final class ProductFilter {

    private String classificationName;
    private String classificationValue;

    public ProductFilter() {
    }

    public String getClassificationName() {
        return classificationName;
    }

    public String getClassificationValue() {
        return classificationValue;
    }

    public void setClassificationName(String classificationName) {
        this.classificationName = classificationName;
    }

    public void setClassificationValue(String classificationValue) {
        this.classificationValue = classificationValue;
    }
}
