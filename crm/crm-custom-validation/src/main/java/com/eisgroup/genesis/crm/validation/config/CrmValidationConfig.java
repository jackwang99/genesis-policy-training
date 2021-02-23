/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.validation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eisgroup.genesis.crm.validation.utils.ApplicationContextUtil;

@Configuration
public class CrmValidationConfig {

   @Bean
   public ApplicationContextUtil applicationContextUtil() {
       return new ApplicationContextUtil();
   }
}
