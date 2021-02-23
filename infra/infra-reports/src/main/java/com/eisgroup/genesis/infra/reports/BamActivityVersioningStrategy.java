/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.infra.reports;

import com.eisgroup.genesis.report.versioning.ColumnAggregator;
import com.eisgroup.genesis.report.versioning.VersioningStrategy;

/**
 * Versioning strategy that resolves bam activity version in report repository.
 *
 * @author mguzelis
 */
public class BamActivityVersioningStrategy implements VersioningStrategy {
    private static final long serialVersionUID = 2932573959318124788L;

    @Override
    public ColumnAggregator[] getVersionAggregators() {
        return new ColumnAggregator[0];
    }

    @Override
    public String[] getGroupByColumns() {
        return new String[]{
                "rootId"
        };
    }
}
