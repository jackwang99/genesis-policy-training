/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.modules.config;

import com.eisgroup.genesis.model.UnderwritingMeasureDomainModel;
import com.eisgroup.genesis.model.ModelResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for strategy lifecycle module beans
 *
 * @author ileanavets
 * @since 1.0
 */
@Configuration
public class UMeasureLifecycleModuleConfig {

    @Bean
    public ModelResolver umeasureModelResolver() {
        return new ModelResolver(
        		UnderwritingMeasureDomainModel.INSTANCE.modelName(),
        		UnderwritingMeasureDomainModel.INSTANCE.modelVersion());
    }
}
