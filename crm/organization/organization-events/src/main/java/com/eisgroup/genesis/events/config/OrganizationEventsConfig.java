/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.events.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eisgroup.genesis.model.ModelResolver;

/**
 * Organization Customer event config
 * 
 * @author avoitau
 *
 */
@Configuration
public class OrganizationEventsConfig {

    @Bean
    public ModelResolver modelResolver() {
        return new ModelResolver("ORGANIZATIONCUSTOMER", "1");
    }

}
