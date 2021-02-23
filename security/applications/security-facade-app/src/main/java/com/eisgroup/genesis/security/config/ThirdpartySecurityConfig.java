/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eisgroup.genesis.security.impl.ThirdPartyAuthenticationResolver;
import com.eisgroup.genesis.security.impl.ThirdPartyTokenAuthenticationProvider;

/**
 * Thirdparty security beans configuration.
 * 
 * @author dstulgis
 */
@Configuration
public class ThirdpartySecurityConfig {
	
	@Bean
	public ThirdPartyAuthenticationResolver thirdpartyAuthenticationResolver() {
		return new ThirdPartyAuthenticationResolver();
	}
	
	@Bean
	public ThirdPartyTokenAuthenticationProvider ThirdpartyTokenAuthenticationProvider() {
	    return new ThirdPartyTokenAuthenticationProvider();
	}
}
