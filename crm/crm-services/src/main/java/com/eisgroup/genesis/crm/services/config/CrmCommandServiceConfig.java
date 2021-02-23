/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.crm.services.config;

import com.eisgroup.genesis.commands.services.CustomerContactService;
import com.eisgroup.genesis.crm.services.customer.CustomerContactServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author Valeriy Sizonenko
 * @since 9.15
 */
@Configuration
public class CrmCommandServiceConfig {

    @Bean
    @Primary
    public CustomerContactService customerContactService() {
        return new CustomerContactServiceImpl();
    }
}