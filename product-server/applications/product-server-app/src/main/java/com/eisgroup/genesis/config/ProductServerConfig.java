/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.config;

import com.eisgroup.genesis.facade.security.config.FacadeSecurityConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.Resource;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * Additional configuration for product server
 *
 * @author azhakhavets
 */
@Configuration
@ImportResource("classpath*:META-INF/spring/*-beans.xml")
public class ProductServerConfig extends FacadeSecurityConfig {

    @Bean
    @ConditionalOnExpression("#{true}")
    public static PropertyPlaceholderConfigurer dynamicPropertyPlaceholderConfigurer(
            @Value("classpath*:META-INF/config/*-default.properties") Resource[] defaults,
            @Value("classpath*:META-INF/config/*-override.properties") Resource[] overrides) {
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setIgnoreResourceNotFound(true);
        configurer.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
        configurer.setLocations(ArrayUtils.addAll(defaults, overrides));
        return configurer;
    }

    @Bean
    public SecurityContextPersistenceFilter securityContextPersistenceFilter() {
        return new SecurityContextPersistenceFilter(securityContextRepository());
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }
}
