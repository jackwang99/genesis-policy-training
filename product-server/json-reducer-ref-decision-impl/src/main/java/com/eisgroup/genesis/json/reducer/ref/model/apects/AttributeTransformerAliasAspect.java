/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.json.reducer.ref.model.apects;

import com.eisgroup.genesis.decision.aspects.AspectAdapter;
import com.eisgroup.genesis.decision.aspects.AspectColumnDescriptor;
import com.eisgroup.genesis.decision.dsl.model.AspectColumn;
import com.eisgroup.genesis.decision.dsl.model.DataType;

/**
 * Specifies new attribute value
 *
 * @author ssauchuk
 * @since 10.2
 */
public class AttributeTransformerAliasAspect implements AspectAdapter {

    public static final String NAME = "alias";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public AspectColumnDescriptor describeAspect(AspectColumn column) {
        return new AspectColumnDescriptor(NAME, DataType.STRING);
    }
}
