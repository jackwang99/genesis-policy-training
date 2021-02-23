/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.modules;

import java.util.HashMap;
import java.util.Map;

import com.eisgroup.genesis.commands.crm.agencycontainer.conversion.IndividualConversionWriteHandler;
import com.eisgroup.genesis.crm.commands.ConversionCommands;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.eisgroup.genesis.commands.crm.agencycontainer.IndividualAgencyContainerWriteHandler;
import com.eisgroup.genesis.commands.publisher.api.CommandPublisher;
import com.eisgroup.genesis.crm.listener.agencycontainer.IndividualAgencyContainerBusinessEntityCommandExecutorListener;
import com.eisgroup.genesis.crm.listener.agencycontainer.IndividualAgencyContainerWriteCommandExecutorListener;
import com.eisgroup.genesis.json.link.EntityLinkResolverRegistry;
import com.eisgroup.genesis.lifecycle.CommandHandler;

/**
 * @author Dmitry Andronchik
 */
@Configuration
@ComponentScan("com.eisgroup.genesis.commands.listener")
public class IndividualAgencyContainerLifecycleModule extends AgencyContainerLifecycleModule {
    
    @Override
    public String getModelName() {
        return "IndividualAgencyContainer";
    }
    
    @Override
    public Map<String, CommandHandler<?, ?>> getCommandHandlers() {
        Map<String, CommandHandler<?, ?>> handlers = new HashMap<>(super.getCommandHandlers());
        handlers.put(IndividualAgencyContainerWriteHandler.NAME, new IndividualAgencyContainerWriteHandler());
        handlers.put(ConversionCommands.WRITE, new IndividualConversionWriteHandler());
        return handlers;
    }
    
    @Bean
    public IndividualAgencyContainerWriteCommandExecutorListener individualAgencyContainerWriteCommandExecutorListener(CommandPublisher commandPublisher, 
            EntityLinkResolverRegistry linkResolverRegistry) {
        return new IndividualAgencyContainerWriteCommandExecutorListener(commandPublisher, linkResolverRegistry);
    }    
    
    @Bean
    public IndividualAgencyContainerBusinessEntityCommandExecutorListener individualAgencyContaineBusinessEntityCommandExecutorListener(CommandPublisher commandPublisher) {
        return new IndividualAgencyContainerBusinessEntityCommandExecutorListener(commandPublisher);
    }    
}
