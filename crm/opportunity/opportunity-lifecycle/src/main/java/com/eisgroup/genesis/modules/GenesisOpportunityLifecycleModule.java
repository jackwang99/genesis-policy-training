/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.modules;

import com.eisgroup.genesis.commands.opportunity.conversion.ConversionWriteHandler;
import com.eisgroup.genesis.commands.opportunity.WriteHandlerRI;
import com.eisgroup.genesis.commands.services.opportunity.OpportunityValidationService;
import com.eisgroup.genesis.crm.commands.ConversionCommands;
import com.eisgroup.genesis.crm.commands.CrmCommands;
import com.eisgroup.genesis.crm.services.opportunity.OpportunityValidationServiceImpl;
import com.eisgroup.genesis.http.factory.HttpClientFactory;
import com.eisgroup.genesis.lifecycle.CommandHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.eisgroup.genesis.commands.services.DefaultOpportunityAssociationProcessor;
import com.eisgroup.genesis.commands.services.DefaultOpportunityNotificationService;
import com.eisgroup.genesis.commands.services.OpportunityAssociationProcessor;
import com.eisgroup.genesis.commands.services.OpportunityNotificationService;
import org.springframework.context.annotation.Primary;

import java.util.Map;


@Configuration
@ComponentScan("com.eisgroup.genesis.commands.listener")
public class GenesisOpportunityLifecycleModule extends OpportunityLifecycleModule {

    @Override
    public String getModelName() {
        return "Opportunity";
    }

    @Override
    public Map<String, CommandHandler<?, ?>> getCommandHandlers() {
        Map<String, CommandHandler<?, ?>> handlers =  super.getCommandHandlers();
        handlers.put(CrmCommands.WRITE_OPPORTUNITY, new WriteHandlerRI());
        handlers.put(ConversionCommands.WRITE, new ConversionWriteHandler());
        return handlers;
    }

    @Bean
    public OpportunityAssociationProcessor opportunityAssociationProcessor() {
        return new DefaultOpportunityAssociationProcessor();
    }
    
    @Bean
    public OpportunityNotificationService opportunityNotificationService(HttpClientFactory httpClientFactory) {
        return new DefaultOpportunityNotificationService(httpClientFactory);
    }

    @Bean
    @Primary
    public OpportunityValidationService opportunityValidationService() {
        return new OpportunityValidationServiceImpl();
    }
}
