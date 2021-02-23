/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.events;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.eisgroup.genesis.registry.core.search.builder.config.SearchIndexingBuilderConfig;

@Import({ SearchIndexingBuilderConfig.class })
@Configuration
public class PartyEventsConfiguration {
}
