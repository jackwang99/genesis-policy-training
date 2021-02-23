/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.modules;

import java.util.Map;

import com.eisgroup.genesis.commands.quote.FleetAutoRateHandler;
import com.eisgroup.genesis.lifecycle.CommandHandler;
import com.eisgroup.genesis.policy.core.lifecycle.commands.listener.IntegrationListenerConfig;
import com.eisgroup.genesis.policy.core.lifecycle.commands.policy.CompletePendingPolicyHandler;
import com.eisgroup.genesis.policy.core.lifecycle.commands.policy.FailPendingPolicyHandler;
import com.eisgroup.genesis.policy.core.lifecycle.commands.quote.IssueFailHandler;
import com.eisgroup.genesis.policy.core.lifecycle.commands.quote.IssueRequestHandler;
import com.eisgroup.genesis.policy.pnc.auto.modules.AbstractAutoPolicyLifecycleModule;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.eisgroup.genesis.commands.listener")
public class FleetAutoLifecycleModule extends AbstractAutoPolicyLifecycleModule {

    @Override
    public Object[] getConfigResources() {
        return ArrayUtils.addAll(super.getConfigResources(), IntegrationListenerConfig.class);
    }

    @Override
    public String getModelName() {
        return "FleetAuto";
    }

    @Override
    public String getStateMachineName() {
        return "FleetAuto";
    }

    @Override
    public Map<String, CommandHandler<?, ?>> getCommandHandlers() {
        Map<String, CommandHandler<?, ?>> handlers = super.getCommandHandlers();
        handlers.putAll(addHandlers(
                new FleetAutoRateHandler(),
                new IssueRequestHandler(),
                new IssueFailHandler(),

                new CompletePendingPolicyHandler(),
                new FailPendingPolicyHandler()));

        return handlers;

    }

}