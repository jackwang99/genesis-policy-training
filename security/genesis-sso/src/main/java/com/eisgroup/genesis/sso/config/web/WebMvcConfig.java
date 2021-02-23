/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.sso.config.web;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;

/**
 * Configuration that is specific to web mvc application's context.
 *
 * @author gvisokinskas
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private PropertyPlaceholderConfigurer propertyPlaceholderConfigurer;

    /**
     * Property placeholder configurers are not inherited in child application contexts,
     * that's why we need to redefine it here.
     */
    @Bean
    @ConditionalOnExpression("#{true}")
    public PropertyPlaceholderConfigurer dynamicPropertyPlaceholderConfigurer(
            @Value("classpath*:META-INF/config/*-default.properties") Resource[] defaults,
            @Value("classpath*:META-INF/config/*-override.properties") Resource[] overrides,
            @Value("classpath:local.properties") Resource local) {
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setIgnoreResourceNotFound(true);
        configurer.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
        configurer.setLocations(ArrayUtils.add(ArrayUtils.addAll(defaults, overrides), local));
        return configurer;
    }

    /**
     * Default controller that returns an empty response instead of a home page.
     * SSO server should not have a user interface.
     */
    @Controller
    public static class DefaultController {
        @RequestMapping(value = "*", method = RequestMethod.GET, produces = {"application/json"})
        @ResponseStatus(HttpStatus.OK)
        public @ResponseBody String empty(HttpServletRequest request) {
            return "{}";
        }
    }
}