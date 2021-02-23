/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.events.config;

import com.eisgroup.genesis.events.EventPublisher;
import com.eisgroup.genesis.events.listener.OrganizationAssigmentsEventValidator;
import com.eisgroup.genesis.factory.utils.JsonEntityRefResolver;
import com.eisgroup.genesis.json.link.EntityLinkBuilderRegistry;
import com.eisgroup.genesis.json.link.EntityLinkResolverRegistry;
import com.eisgroup.genesis.json.link.config.EntityLinkBuilderSpringConifg;
import com.eisgroup.genesis.model.external.ExternalModelRepository;
import com.eisgroup.genesis.orgstruct.repository.api.OrganizationStructureRepository;
import com.eisgroup.genesis.policy.core.lifecycle.commands.QuoteCommands;
import com.eisgroup.genesis.policy.core.lifecycle.config.PolicyIntegrationApiConfig;
import com.eisgroup.genesis.policy.core.lifecycle.config.PolicyMessageApiConfig;
import com.eisgroup.genesis.search.events.EntityIndexingConfiguration;
import com.eisgroup.genesis.streams.consumer.config.StreamConsumerSecurityConfig;
import com.eisgroup.genesis.streams.publisher.MessagePublisher;
import com.eisgroup.genesis.streams.publisher.config.StreamPublisherSecurityConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Predicate;

/**
 * Contains bean configurations of org-struct event handlers. 
 * 
 * @author astasauskas, adainelis
 *
 */
@Configuration
@Import({ EntityLinkBuilderSpringConifg.class, 
    StreamPublisherSecurityConfig.class, StreamConsumerSecurityConfig.class,
    PolicyIntegrationApiConfig.class, PolicyMessageApiConfig.class })
public class AllEventsConfig {

    @Bean
    public JsonEntityRefResolver jsonRefResolver() {
        return new JsonEntityRefResolver();
    }

    
    @Bean
    public EventPublisher eventPublisher(MessagePublisher streamMessagePublisher) {
        return new EventPublisher(streamMessagePublisher);
    }

    @Bean
    public EntityIndexingConfiguration entityIndexingConfiguration(@Value("${genesis.search.indexing.exclude:Party}") String[] moduleTypesToSkip) {
        return new EntityIndexingConfiguration(Collections.singleton(EntityIndexingConfiguration.MATCHES_ALL),
                new HashSet<String>(Arrays.asList(moduleTypesToSkip)));
    }

    @Bean
    @DependsOn("organizationStructureRepository")
    public OrganizationAssigmentsEventValidator organizationAssigmentEventValidator (EntityLinkBuilderRegistry linkBuilderRegistry,
            EntityLinkResolverRegistry entityLinkResolverRegistry, MessagePublisher messagePublisher, ExternalModelRepository externalModelRepo,
            OrganizationStructureRepository organizationStructureRepository, @Value("${genesis.orgPerson.model}")String orgPersonModelName) {

        final Predicate<String> filter = QuoteCommands.ISSUE_REQUEST::equals;

        return new OrganizationAssigmentsEventValidator(filter, linkBuilderRegistry, entityLinkResolverRegistry, messagePublisher,
                externalModelRepo, organizationStructureRepository, orgPersonModelName);
    }
   
}
