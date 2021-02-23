/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.events.config;

import com.eisgroup.genesis.events.EventPublisher;
import com.eisgroup.genesis.factory.utils.JsonEntityRefResolver;
import com.eisgroup.genesis.json.link.config.EntityLinkBuilderSpringConifg;
import com.eisgroup.genesis.streams.consumer.config.StreamConsumerSecurityConfig;
import com.eisgroup.genesis.streams.publisher.MessagePublisher;
import com.eisgroup.genesis.streams.publisher.config.StreamPublisherSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author astasauskas
 */
@Configuration
@Import({EntityLinkBuilderSpringConifg.class,
        StreamPublisherSecurityConfig.class, StreamConsumerSecurityConfig.class})
public class AllEventsConfig {

    @Bean
    public JsonEntityRefResolver jsonRefResolver() {
        return new JsonEntityRefResolver();
    }

    @Bean
    public EventPublisher eventPublisher(MessagePublisher streamMessagePublisher) {
        return new EventPublisher(streamMessagePublisher);
    }
}
