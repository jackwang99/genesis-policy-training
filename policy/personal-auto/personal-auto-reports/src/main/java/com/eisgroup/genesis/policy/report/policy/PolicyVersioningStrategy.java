/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.report.policy;

import com.eisgroup.genesis.report.versioning.ColumnAggregator;
import com.eisgroup.genesis.report.versioning.ColumnAggregator.AggregatorType;
import com.eisgroup.genesis.report.versioning.VersioningStrategy;

/**
 * Versioning strategy that resolves current policy version in report repository
 * 
 * @author azukovskij
 *
 */
public class PolicyVersioningStrategy implements VersioningStrategy {
    
    private static final long serialVersionUID = 5759772028379695203L;


    public ColumnAggregator[] getVersionAggregators() {
        return new ColumnAggregator[] {
                new ColumnAggregator("txType", AggregatorType.MAX)
        };
    }

    public String[] getGroupByColumns() {
        return new String[] {
                "txType", "rootId"
        };
    }
    
}
