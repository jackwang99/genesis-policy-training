/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.events.config;

import com.eisgroup.genesis.commands.publisher.api.CommandPublisher;
import com.eisgroup.genesis.events.EventPublisher;
import com.eisgroup.genesis.factory.utils.JsonEntityRefResolver;
import com.eisgroup.genesis.integration.billing.config.EISBillingIntegrationConfig;
import com.eisgroup.genesis.integration.billing.config.MockBillingIntegrationConfig;
import com.eisgroup.genesis.json.link.EntityLinkResolverRegistry;
import com.eisgroup.genesis.json.link.config.EntityLinkBuilderSpringConifg;
import com.eisgroup.genesis.policy.core.lifecycle.config.PolicyIntegrationApiConfig;
import com.eisgroup.genesis.policy.core.lifecycle.config.PolicyMessageApiConfig;
import com.eisgroup.genesis.policy.core.lifecycle.listener.FailPendingPolicyListener;
import com.eisgroup.genesis.policy.core.lifecycle.listener.SucceedPendingPolicyListener;
import com.eisgroup.genesis.policy.core.umeasure.services.UMeasureDefaultConfig;
import com.eisgroup.genesis.search.events.EntityIndexingConfiguration;
import com.eisgroup.genesis.streams.consumer.config.StreamConsumerSecurityConfig;
import com.eisgroup.genesis.streams.publisher.MessagePublisher;
import com.eisgroup.genesis.streams.publisher.config.StreamPublisherSecurityConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * Policy events application configuration.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
@Configuration
@Import({
    EntityLinkBuilderSpringConifg.class, StreamPublisherSecurityConfig.class, StreamConsumerSecurityConfig.class,
    EISBillingIntegrationConfig.class, MockBillingIntegrationConfig.class, PolicyIntegrationApiConfig.class, PolicyMessageApiConfig.class,
    UMeasureDefaultConfig.class
})
public class PolicyEventsConfig {

    @Bean
    public JsonEntityRefResolver jsonRefResolver() {
        return new JsonEntityRefResolver();
    }

    @Bean
    public EventPublisher eventPublisher(MessagePublisher streamMessagePublisher) {
        return new EventPublisher(streamMessagePublisher);
    }

    @Bean
    public SucceedPendingPolicyListener succeedPendingPolicyListener(CommandPublisher commandPublisher, EntityLinkResolverRegistry entityLinkResolverRegistry) {
        return new SucceedPendingPolicyListener(commandPublisher, entityLinkResolverRegistry);
    }

    @Bean
    public FailPendingPolicyListener failPendingPolicyListener(CommandPublisher commandPublisher, EntityLinkResolverRegistry entityLinkResolverRegistry) {
        return new FailPendingPolicyListener(commandPublisher, entityLinkResolverRegistry);
    }

    @Bean
    public EntityIndexingConfiguration entityIndexingConfiguration(@Value("${genesis.search.indexing.exclude:Party}") String[] moduleTypesToSkip) {
        return new EntityIndexingConfiguration(Collections.singleton(EntityIndexingConfiguration.MATCHES_ALL),
                new HashSet<>(Arrays.asList(moduleTypesToSkip)));
    }
}
