/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.design.tooling.facade.dto;

import java.util.Collection;
import java.util.List;

import com.eisgroup.genesis.design.tooling.model.Classification;
import com.eisgroup.genesis.design.tooling.model.Product;

/**
 * GraphQL representation of the {@link Product}
 *
 * @author dlevchuk
 * @since 10.10
 */
public final class ProductDTO {
    private final String productCd;
    private final Collection<Classification> classifications;

    /**
     * Copy constructor
     */
    public ProductDTO(Product product) {
        this.productCd = product.getProductCd();
        this.classifications = List.copyOf(product.getClassifications());
    }

    public String getProductCd() {
        return productCd;
    }

    public Collection<Classification> getClassifications() {
        return classifications;
    }
}
