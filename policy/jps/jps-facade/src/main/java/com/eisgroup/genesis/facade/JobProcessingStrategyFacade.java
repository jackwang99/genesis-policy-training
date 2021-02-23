/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.facade;

import com.eisgroup.genesis.model.JobProcessingStrategyDomainModel;
import com.eisgroup.genesis.policy.core.jps.facade.AbstractJobProcessingStrategyFacade;

/**
 * Facade module for {@link com.eisgroup.genesis.factory.modeling.types.JobProcessingStrategy}
 *
 * @author ileanavets
 * @since 1.0
 */
public class JobProcessingStrategyFacade extends AbstractJobProcessingStrategyFacade {

    @Override
    public String getModelType() {
        return JobProcessingStrategyDomainModel.INSTANCE.modelType();
    }

    @Override
    public String getModelName() {
        return JobProcessingStrategyDomainModel.INSTANCE.modelName();
    }

    @Override
    public String getModelVersion() {
        return JobProcessingStrategyDomainModel.INSTANCE.modelVersion();
    }

    @Override
    public int getFacadeVersion() {
        return 1;
    }

}
