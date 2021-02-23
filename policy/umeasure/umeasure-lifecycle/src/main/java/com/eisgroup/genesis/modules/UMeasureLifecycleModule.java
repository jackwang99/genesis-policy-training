/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.modules;

import com.eisgroup.genesis.model.UnderwritingMeasureDomainModel;

import com.eisgroup.genesis.policy.core.umeasure.lifecycle.modules.AbstractUMeasureLifecycleModule;
import org.springframework.context.annotation.Configuration;

/**
 * Lifecycle module for {@link com.eisgroup.genesis.factory.modeling.types.UnderwritingMeasure}
 *
 * @author aspichakou
 * @since 1.0
 */
@Configuration
public class UMeasureLifecycleModule extends AbstractUMeasureLifecycleModule {

    @Override
    public String getModelType() {
        return UnderwritingMeasureDomainModel.INSTANCE.modelType();
    }

    @Override
    public String getModelName() {
        return UnderwritingMeasureDomainModel.INSTANCE.modelName();
    }

    @Override
    public String getModelVersion() {
        return UnderwritingMeasureDomainModel.INSTANCE.modelVersion();
    }

}
