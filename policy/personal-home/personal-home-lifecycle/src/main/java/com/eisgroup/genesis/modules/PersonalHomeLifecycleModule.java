/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.modules;

import com.eisgroup.genesis.commands.quote.PersonalHomeRatingHandler;
import com.eisgroup.genesis.lifecycle.CommandHandler;
import com.eisgroup.genesis.policy.core.lifecycle.modules.PolicyLifecycleModule;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ComponentScan("com.eisgroup.genesis.commands.listener")
public class PersonalHomeLifecycleModule extends PolicyLifecycleModule {

    @Override
    public Object[] getConfigResources() {
        return ArrayUtils.addAll(super.getConfigResources());
    }
    
    @Override
    public String getModelName() {
        return "PersonalHome";
    }
    
    @Override
    public String getStateMachineName() {
        return "PersonalHomePolicy";
    }

    @Override
    public Map<String, CommandHandler<?, ?>> getCommandHandlers() {
        Map<String, CommandHandler<?, ?>> handlers = super.getCommandHandlers();

        handlers.putAll(addHandlers(new PersonalHomeRatingHandler()));

        return handlers;
    }
}