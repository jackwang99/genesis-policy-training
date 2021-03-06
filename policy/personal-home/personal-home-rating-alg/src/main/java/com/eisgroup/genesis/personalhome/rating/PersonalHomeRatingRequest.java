/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.personalhome.rating;

import org.openl.rules.context.IRulesRuntimeContext;

import com.eisgroup.rating.input.impl.DefaultBusinessNode;

/**
 * Income object for the Personal Home rating
 *
 * @author zhchen
 * @since 1.0
 */
public class PersonalHomeRatingRequest {

    private IRulesRuntimeContext context;

    private DefaultBusinessNode node;

    public IRulesRuntimeContext getContext() {
        return context;
    }

    public void setContext(IRulesRuntimeContext context) {
        this.context = context;
    }

    public DefaultBusinessNode getNode() {
        return node;
    }

    public void setNode(DefaultBusinessNode node) {
        this.node = node;
    }
}
