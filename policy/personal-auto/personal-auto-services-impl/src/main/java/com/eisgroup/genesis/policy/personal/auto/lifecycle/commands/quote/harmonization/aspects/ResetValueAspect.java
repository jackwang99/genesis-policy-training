/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.aspects;

import com.eisgroup.genesis.decision.aspects.AspectAdapter;
import com.eisgroup.genesis.decision.aspects.AspectColumnDescriptor;
import com.eisgroup.genesis.decision.dsl.model.AspectColumn;
import com.eisgroup.genesis.decision.dsl.model.DataType;

/**
 * Specifies default value
 *
 * @author sbelauski
 */
public class ResetValueAspect implements AspectAdapter {

    public static final String NAME = "resetValue";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public AspectColumnDescriptor describeAspect(AspectColumn column) {
        return new AspectColumnDescriptor(NAME, DataType.STRING);
    }
}