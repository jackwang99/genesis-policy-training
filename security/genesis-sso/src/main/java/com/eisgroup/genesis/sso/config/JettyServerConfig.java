/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.sso.config;

import com.eisgroup.genesis.boot.BootsrapBean;
import com.eisgroup.genesis.sso.server.JettyBootstrap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Integration with existing bootstrap api provided by platform.
 *
 * @author gvisokinskas
 */
@Configuration
public class JettyServerConfig {
    @Bean
    public BootsrapBean jettyBootstrap() {
        return new JettyBootstrap();
    }
}