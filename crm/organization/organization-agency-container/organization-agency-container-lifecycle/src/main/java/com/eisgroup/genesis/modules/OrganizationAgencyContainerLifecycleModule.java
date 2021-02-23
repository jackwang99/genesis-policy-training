/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.modules;

import java.util.HashMap;
import java.util.Map;

import com.eisgroup.genesis.commands.crm.agencycontainer.conversion.OrganizationConversionWriteHandler;
import com.eisgroup.genesis.crm.commands.ConversionCommands;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.eisgroup.genesis.commands.crm.agencycontainer.OrganizationAgencyContainerWriteHandler;
import com.eisgroup.genesis.commands.publisher.api.CommandPublisher;
import com.eisgroup.genesis.crm.listener.agencycontainer.OrganizationAgencyContainerBusinessEntityHandler;
import com.eisgroup.genesis.crm.listener.agencycontainer.OrganizationAgencyContainerWriteCommandExecutorListener;
import com.eisgroup.genesis.json.link.EntityLinkResolverRegistry;
import com.eisgroup.genesis.lifecycle.CommandHandler;

/**
 * @author Dmitry Andronchik
 */
@Configuration
@ComponentScan("com.eisgroup.genesis.commands.listener")
public class OrganizationAgencyContainerLifecycleModule extends AgencyContainerLifecycleModule {

    @Override
    public String getModelName() {
        return "OrganizationAgencyContainer";
    }
    
    @Override
    public Map<String, CommandHandler<?, ?>> getCommandHandlers() {
        Map<String, CommandHandler<?, ?>> handlers = new HashMap<>(super.getCommandHandlers());
        handlers.put(OrganizationAgencyContainerWriteHandler.NAME, new OrganizationAgencyContainerWriteHandler());
        handlers.put(ConversionCommands.WRITE, new OrganizationConversionWriteHandler());
        return handlers;
    }
    
    @Bean
    public OrganizationAgencyContainerWriteCommandExecutorListener organizationAgencyContainerWriteCommandExecutorListener(CommandPublisher commandPublisher, 
            EntityLinkResolverRegistry linkResolverRegistry) {
        return new OrganizationAgencyContainerWriteCommandExecutorListener(commandPublisher, linkResolverRegistry);
    }
    
    @Bean
    public OrganizationAgencyContainerBusinessEntityHandler organizationAgencyContainerBusinessEntityCommandExecutorListener(CommandPublisher commandPublisher) {
        return new OrganizationAgencyContainerBusinessEntityHandler(commandPublisher);
    }    
}
