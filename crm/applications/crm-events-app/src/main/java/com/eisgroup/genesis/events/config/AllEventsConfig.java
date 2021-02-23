/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.events.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Predicate;

import com.eisgroup.genesis.policy.core.lifecycle.config.PolicyMessageApiConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.eisgroup.genesis.commands.publisher.api.CommandPublisher;
import com.eisgroup.genesis.crm.events.listener.CustomerCommandEventHandler;
import com.eisgroup.genesis.crm.events.listener.InitCustomerPolicyInfoHandler;
import com.eisgroup.genesis.crm.events.listener.NewBusinessIssueCrmHandler;
import com.eisgroup.genesis.crm.events.listener.OrganizationWriteCrmHandler;
import com.eisgroup.genesis.crm.events.listener.OrganizationalPersonWriteCrmHandler;
import com.eisgroup.genesis.crm.events.listener.ProductCreationCrmHandler;
import com.eisgroup.genesis.crm.events.listener.propagation.CemStartPropagationEventHandler;
import com.eisgroup.genesis.events.EventPublisher;
import com.eisgroup.genesis.factory.repository.links.config.FactoryLinkResolverConfig;
import com.eisgroup.genesis.factory.utils.JsonEntityRefResolver;
import com.eisgroup.genesis.json.link.EntityLinkBuilderRegistry;
import com.eisgroup.genesis.json.link.EntityLinkResolverRegistry;
import com.eisgroup.genesis.json.link.EntityResolver;
import com.eisgroup.genesis.json.link.config.EntityLinkBuilderSpringConifg;
import com.eisgroup.genesis.model.external.ExternalModelRepository;
import com.eisgroup.genesis.orgstruct.commands.CommandNames;
import com.eisgroup.genesis.policy.core.lifecycle.commands.QuoteCommands;
import com.eisgroup.genesis.policy.core.lifecycle.config.PolicyIntegrationApiConfig;
import com.eisgroup.genesis.rdf.repository.RelationshipRepository;
import com.eisgroup.genesis.search.events.EntityIndexingConfiguration;
import com.eisgroup.genesis.streams.consumer.config.StreamConsumerSecurityConfig;
import com.eisgroup.genesis.streams.publisher.MessagePublisher;
import com.eisgroup.genesis.streams.publisher.config.StreamPublisherSecurityConfig;
import com.google.common.collect.ImmutableMap;


/**
 * @author astasauskas
 */
@Configuration
@Import({EntityLinkBuilderSpringConifg.class, 
        StreamPublisherSecurityConfig.class, StreamConsumerSecurityConfig.class, FactoryLinkResolverConfig.class,
        PolicyIntegrationApiConfig.class, PolicyMessageApiConfig.class })
public class AllEventsConfig {
    
    @Value("${entity.index.confidential:false}")
    private boolean indexConfidential;    

    @Bean
    public JsonEntityRefResolver jsonRefResolver() {
        return new JsonEntityRefResolver();
    }

    @Bean
    public EventPublisher eventPublisher(MessagePublisher streamMessagePublisher) {
        return new EventPublisher(streamMessagePublisher);
    }
    
    @Bean
    public CustomerCommandEventHandler customerCommandEventHandler(CommandPublisher commandPublisher) {
        return new CustomerCommandEventHandler(commandPublisher);
    }

    @Bean
    public ProductCreationCrmHandler productCreationHandler(EntityLinkBuilderRegistry linkBuilderRegistry, EntityResolver entityResolver, MessagePublisher messagePublisher,
            ExternalModelRepository externalModelRepo) {
        Predicate<String> filter = commandName -> QuoteCommands.ISSUE_REQUEST.equals(commandName);
        return new ProductCreationCrmHandler(filter, linkBuilderRegistry, messagePublisher, entityResolver, externalModelRepo);
    }

    @Bean
    public NewBusinessIssueCrmHandler newBusinessIssueCrmHandler(EntityLinkBuilderRegistry linkBuilderRegistry,
                                                                 RelationshipRepository rdfRepo, CommandPublisher commandPublisher,
                                                                 ExternalModelRepository externalModelRepo) {
        Predicate<String> filter = commandName -> QuoteCommands.NEW_BUSINESS_ISSUE.equals(commandName);
        return new NewBusinessIssueCrmHandler(filter, linkBuilderRegistry, rdfRepo, commandPublisher,
                externalModelRepo);
    }

    @Bean
    public EntityIndexingConfiguration entityIndexingConfiguration(@Value("${genesis.search.indexing.exclude:Party}") String[] moduleTypesToSkip) {
        return new EntityIndexingConfiguration(Collections.singleton(EntityIndexingConfiguration.MATCHES_ALL),
                new HashSet<String>(Arrays.asList(moduleTypesToSkip)), indexConfidential);
    }
    
    @Bean
    public OrganizationalPersonWriteCrmHandler organizationalPersonWriteCrmHandler(CustomerCommandEventHandler customerCommandEventHandler, ExternalModelRepository externalModelRepo) {
        Predicate<String> filter = commandName -> CommandNames.ORGANIZATIONAL_PERSON_WRITE.getName().equals(commandName);
        return new OrganizationalPersonWriteCrmHandler("INDIVIDUALCUSTOMER", "1", filter, externalModelRepo, customerCommandEventHandler);
    }
    
    @Bean
    public OrganizationWriteCrmHandler organizationWriteCrmHandler(CustomerCommandEventHandler customerCommandEventHandler, ExternalModelRepository externalModelRepo) {
        Predicate<String> filter = commandName -> CommandNames.ORGANIZATION_WRITE.getName().equals(commandName);
        return new OrganizationWriteCrmHandler("ORGANIZATIONCUSTOMER", "1", "LLC", "524210", filter, externalModelRepo, customerCommandEventHandler);
    }
    
    @Bean
    public InitCustomerPolicyInfoHandler initCustomerPolicyInfoHandler(EntityLinkBuilderRegistry linkBuilderRegistry, CustomerCommandEventHandler customerCommandEventHandler,
                                                                ExternalModelRepository externalModelRepo) {
        Predicate<String> filter = commandName -> QuoteCommands.NEW_BUSINESS_ISSUE.equals(commandName);
        return new InitCustomerPolicyInfoHandler(filter, linkBuilderRegistry, customerCommandEventHandler, externalModelRepo,
                                    ImmutableMap.of("INDIVIDUALCUSTOMER", "IndividualAgencyContainer", "ORGANIZATIONCUSTOMER", "OrganizationAgencyContainer"));
    }
    
    @Bean
    public CemStartPropagationEventHandler cemStartPropagationEventHandler(EntityLinkResolverRegistry linkResolverRegistry, CustomerCommandEventHandler customerCommandEventHandler, 
            EventPublisher eventPublisher) {
        return new CemStartPropagationEventHandler(linkResolverRegistry, customerCommandEventHandler, eventPublisher);
    }        
}
