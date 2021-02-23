/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.modules;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eisgroup.genesis.mapping.RoleDetailsMapper;
import com.eisgroup.genesis.orgstruct.mapping.DefaultOrganizationRoleDetailsMapper;
import com.eisgroup.genesis.spring.delegate.DelegatingBean;

/**
 * @author dlevchuk
 */
@Configuration
public class GenesisOrgStructOrganizationLifecycleModule extends OrganizationLifecycleModule {

    public String getModelName() {
        return "Organization";
    }

    @ConditionalOnMissingBean(ignored = DelegatingBean.class)
	@Bean
	public RoleDetailsMapper roleDetailsMapper() {
		return new DefaultOrganizationRoleDetailsMapper();
	}
    
}
