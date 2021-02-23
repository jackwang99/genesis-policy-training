/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.modules;

import com.eisgroup.genesis.commands.majoraccount.conversion.ConversionWriteHandler;
import com.eisgroup.genesis.crm.commands.ConversionCommands;
import com.eisgroup.genesis.lifecycle.CommandHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ComponentScan("com.eisgroup.genesis.commands.listener")
public class GenesisMajorAccountLifecycleModule extends MajorAccountLifecycleModule {

    @Override
    public Map<String, CommandHandler<?, ?>> getCommandHandlers() {
        Map<String, CommandHandler<?, ?>> handlers = super.getCommandHandlers();
        handlers.put(ConversionCommands.WRITE, new ConversionWriteHandler());
        return handlers;
    }

    @Override
    public String getModelName() {
        return "MajorAccount";
    }

}
