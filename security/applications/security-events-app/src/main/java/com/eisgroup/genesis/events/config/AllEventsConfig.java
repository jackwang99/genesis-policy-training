/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.events.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.eisgroup.genesis.factory.utils.JsonEntityRefResolver;
import com.eisgroup.genesis.json.link.config.EntityLinkBuilderSpringConifg;
import com.eisgroup.genesis.search.events.EntityIndexingConfiguration;
import com.eisgroup.genesis.streams.consumer.config.StreamConsumerSecurityConfig;

/**
 * 
 * @author astasauskas
 *
 */
@Configuration
@Import({ EntityLinkBuilderSpringConifg.class, StreamConsumerSecurityConfig.class })
public class AllEventsConfig {

    @Bean
    public JsonEntityRefResolver jsonRefResolver() {
        return new JsonEntityRefResolver();
    }

    @Bean
    public EntityIndexingConfiguration entityIndexingConfiguration(@Value("${genesis.search.indexing.exclude:Party}") String[] moduleTypesToSkip) {
        return new EntityIndexingConfiguration(Collections.singleton(EntityIndexingConfiguration.MATCHES_ALL),
                new HashSet<String>(Arrays.asList(moduleTypesToSkip)));
    }
}
