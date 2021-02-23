/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.model;

/**
 * Constant class which contains domain model specific info for {@link com.eisgroup.genesis.factory.modeling.types.JobProcessingStrategy}
 *
 * @author ileanavets
 * @since 1.0
 */
public enum JobProcessingStrategyDomainModel {

    INSTANCE;

    public final String modelName() {
        return "JobProcessingStrategy";
    }

    public final String modelType() {
        return modelName();
    }

    public final String modelVersion() {
        return "1";
    }
}
