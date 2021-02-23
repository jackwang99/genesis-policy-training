/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.party.deployer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eisgroup.genesis.entity.metadata.registry.EntityRegistry;
import com.eisgroup.genesis.json.columnstore.ModeledEntityConfigurationProvider;

/**
 * Configuration for party deployment
 *
 * @author mslepikas
 */
@Configuration
public class PartyDeployerConfiguration {

    @Bean
    public ModeledEntityConfigurationProvider modeledEntityConfigurationProvider(EntityRegistry entityRegistry) {
        return new ModeledEntityConfigurationProvider(entityRegistry);
    }
}
