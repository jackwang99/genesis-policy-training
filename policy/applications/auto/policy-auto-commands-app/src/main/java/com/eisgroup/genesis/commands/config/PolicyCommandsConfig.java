/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.commands.config;

import com.eisgroup.genesis.registry.core.common.integration.decision.voter.CommandMetadataBasedVoter;
import com.eisgroup.genesis.registry.core.common.integration.decision.voter.RegistryIntegrationVoter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eisgroup.genesis.policy.core.lifecycle.commands.QuoteCommands;
import com.eisgroup.genesis.policy.core.model.PolicyVariations;

/**
 * Configuration for auto policy commands.
 *
 * @author mslepikas
 * @since 1.0
 */
@Configuration
public class PolicyCommandsConfig {

    /**
     * Disables registry integration for listed quote commands.
     */
    @Bean
    public CommandMetadataBasedVoter quoteCommandsIntegrationVoter() {
        return new CommandMetadataBasedVoter(RegistryIntegrationVoter.Vote.DENY, PolicyVariations.QUOTE,
                QuoteCommands.RATE);
    }

}
