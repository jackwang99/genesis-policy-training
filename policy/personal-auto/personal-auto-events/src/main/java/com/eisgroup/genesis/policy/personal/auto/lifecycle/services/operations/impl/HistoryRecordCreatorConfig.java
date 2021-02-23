/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.lifecycle.services.operations.impl;

import com.eisgroup.genesis.json.link.EntityLinkBuilderRegistry;
import com.eisgroup.genesis.policy.core.services.operations.OperationHistoryRecordCreator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yratkevich
 * @since 9.16
 */
@Configuration
public class HistoryRecordCreatorConfig {

    @Bean
    public OperationHistoryRecordCreator completeOfferOperationHistoryRecordCreator(EntityLinkBuilderRegistry entityLinkBuilderRegistry) {
        return new CompleteOfferOperationHistoryRecordCreator(entityLinkBuilderRegistry);
    }
}