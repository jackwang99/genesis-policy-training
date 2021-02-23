/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.orgstruct.config;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.eisgroup.genesis.facade.OrgStructOrganizationFacade;
import com.eisgroup.genesis.facade.OrganizationalPersonFacade;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;
import com.eisgroup.genesis.factory.repository.links.FactoryLinkResolver;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.mapping.RoleDetailsMapper;
import com.eisgroup.genesis.model.Variation;
import com.eisgroup.genesis.orgstruct.mapping.DefaultOrganizationRoleDetailsMapper;
import com.eisgroup.genesis.repository.ReadContext;
import com.eisgroup.genesis.security.AuthorizationService;
import com.eisgroup.genesis.security.roles.OrgStructPrivilegesHolder;
import com.eisgroup.genesis.spring.delegate.DelegatingBean;

import io.reactivex.Observable;

/**
 * Organization beans configuration
 * 
 * @author dstulgis
 */
@Configuration
public class OrganizationConfig {

	@ConditionalOnMissingBean(ignored = DelegatingBean.class)
	@Bean
	public RoleDetailsMapper roleDetailsMapper() {
		return new DefaultOrganizationRoleDetailsMapper();
	}
	
	@Bean
	@Primary
	public FactoryLinkResolver factoryLinkResolver() {
        return new OrgStructFactoryLinkResolver();
    }	
	
	public static class OrgStructFactoryLinkResolver extends FactoryLinkResolver {

	    @Autowired
	    private AuthorizationService authorizationService;
	    
	    @Override
	    protected Observable<RootEntity> doLoad(DomainModel model, Variation variation, Collection<RootEntityKey> keys,
	            Class<RootEntity> entityClass, ReadContext ctx) {
            secure(model);
	        return super.doLoad(model, variation, keys, entityClass, ctx);
	    }
	    
	    @Override
	    protected Observable<RootEntity> doLoad(DomainModel model, Variation variation, RootEntityKey key,
	            Class<RootEntity> entityClass, ReadContext ctx) {
            secure(model);
	        return super.doLoad(model, variation, key, entityClass, ctx);
	    }
	    
        protected void secure(DomainModel model) {
            if(OrgStructOrganizationFacade.MODEL_NAME.equals(model.getName())) {
                authorizationService.secure(OrgStructPrivilegesHolder.ORGANIZATION_LOAD);
            } else if(OrganizationalPersonFacade.MODEL_NAME.equals(model.getName())) {
                authorizationService.secure(OrgStructPrivilegesHolder.ORGANIZATIONAL_PERSON_LOAD);
            }
        }
	    
	}
	
}
