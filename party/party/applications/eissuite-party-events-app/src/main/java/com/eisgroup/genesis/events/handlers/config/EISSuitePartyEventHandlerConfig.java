/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.events.handlers.config;

import com.eisgroup.genesis.eisintegration.DefaultMapper;
import com.eisgroup.genesis.eisintegration.EISClient;
import com.eisgroup.genesis.eisintegration.PartyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eisgroup.genesis.events.handlers.EISSuitePartyEventHandler;

/**
 * Spring configuration for {@link EISSuitePartyEventHandler}
 */
@Configuration
public class EISSuitePartyEventHandlerConfig {

    @Bean
    public PartyMapper partyMapper() {
        return new DefaultMapper();
    }

    @Bean
    public EISClient eisClient() {
        return new EISClient();
    }

    @Bean
    public EISSuitePartyEventHandler eisSuitePartyEventHandler() {
        return new EISSuitePartyEventHandler();
    }
}
