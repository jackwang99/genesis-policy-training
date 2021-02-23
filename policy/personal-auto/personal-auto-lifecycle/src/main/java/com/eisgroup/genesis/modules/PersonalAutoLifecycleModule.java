/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.modules;

import com.eisgroup.genesis.commands.quote.PersonalAutoRateHandler;
import com.eisgroup.genesis.lifecycle.CommandHandler;
import com.eisgroup.genesis.packaging.offer.listener.PersonalAutoOfferManagerConfig;
import com.eisgroup.genesis.policy.core.lifecycle.commands.listener.IntegrationListenerConfig;
import com.eisgroup.genesis.policy.core.lifecycle.commands.policy.CompletePendingPolicyHandler;
import com.eisgroup.genesis.policy.core.lifecycle.commands.policy.FailPendingPolicyHandler;
import com.eisgroup.genesis.policy.core.lifecycle.commands.quote.IssueFailHandler;
import com.eisgroup.genesis.policy.core.lifecycle.commands.quote.IssueRequestHandler;
import com.eisgroup.genesis.policy.core.lifecycle.commands.quote.MoveFromOptionHandler;
import com.eisgroup.genesis.policy.core.lifecycle.commands.quote.MoveToOptionHandler;
import com.eisgroup.genesis.policy.core.lifecycle.commands.quote.OptionCopyHandler;
import com.eisgroup.genesis.policy.core.lifecycle.commands.quote.OptionCopyStepHandler;
import com.eisgroup.genesis.policy.core.lifecycle.commands.quote.SynchronizeOptionHandler;
import com.eisgroup.genesis.policy.core.lifecycle.commands.quote.SynchronizeVersionHandler;
import com.eisgroup.genesis.policy.core.lifecycle.commands.quote.SynchronizeWithVersionHandler;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.CompleteDataGatherHandler;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.CompleteOfferHandler;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.HarmonizeHandler;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.OrderReportsHandler;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.PersonalAutoOfferImpactConfig;
import com.eisgroup.genesis.policy.pnc.auto.modules.AbstractAutoPolicyLifecycleModule;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ComponentScan("com.eisgroup.genesis.commands.listener")
public class PersonalAutoLifecycleModule extends AbstractAutoPolicyLifecycleModule {

    @Override
    public Object[] getConfigResources() {
        return ArrayUtils.addAll(super.getConfigResources(), IntegrationListenerConfig.class,
                PersonalAutoOfferManagerConfig.class, PersonalAutoOfferImpactConfig.class);
    }

    @Override
    public String getModelName() {
        return "PersonalAuto";
    }

    @Override
    public String getStateMachineName() {
        return "PersonalAutoPolicy";
    }

    @Override
    public Map<String, CommandHandler<?, ?>> getCommandHandlers() {
        Map<String, CommandHandler<?, ?>> handlers = super.getCommandHandlers();
        handlers.putAll(addHandlers(
                new PersonalAutoRateHandler(),
                new OrderReportsHandler(),
                new IssueRequestHandler(),
                new IssueFailHandler(),
                new CompleteDataGatherHandler(),
                new CompleteOfferHandler(),
                new HarmonizeHandler(),
                new MoveToOptionHandler(),
                new MoveFromOptionHandler(),
                new OptionCopyHandler(),
                new OptionCopyStepHandler(),
                new SynchronizeVersionHandler(),
                new SynchronizeWithVersionHandler(),
                new SynchronizeOptionHandler(),
                
                new CompletePendingPolicyHandler(),
                new FailPendingPolicyHandler()));

        return handlers;

    }

}
