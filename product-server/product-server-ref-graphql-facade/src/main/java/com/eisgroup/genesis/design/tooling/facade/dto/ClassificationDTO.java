/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.design.tooling.facade.dto;

import java.util.Collection;
import java.util.List;

import com.eisgroup.genesis.design.tooling.model.Classification;

/**
 * GraphQL representation of the {@link Classification}
 *
 * @author dlevchuk
 * @since 10.10
 */
public final class ClassificationDTO {

    private final String name;
    private final Collection<String> values;

    /**
     * Mapping constructor
     */
    public ClassificationDTO(Classification classification) {
       this.name = classification.getName();
       this.values = List.copyOf(classification.getValues());
    }

    public ClassificationDTO(String name, Collection<String> values) {
        this.name = name;
        this.values = List.copyOf(values);
    }

    public String getName() {
        return name;
    }

    public Collection<String> getValues() {
        return values;
    }
}
