/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.facade;

import org.springframework.beans.factory.annotation.Autowired;

import com.eisgroup.genesis.facade.search.entity.SearchDescriptor;
import com.eisgroup.genesis.facade.search.entity.SearchEntityRequest;
import com.eisgroup.genesis.facade.search.entity.SearchEntityResponse;
import com.eisgroup.genesis.facade.search.entity.SearchFacade;
import com.eisgroup.genesis.security.AuthorizationService;
import com.eisgroup.genesis.security.roles.OrgStructPrivilegesHolder;

import io.reactivex.Single;

public class OrgStructSearchFacade extends SearchFacade {

    @Autowired
    private AuthorizationService authorizationService;
    
    @Override
    public Single<SearchEntityResponse> loadRoot(SearchEntityRequest searchReq) {
        SearchDescriptor.resolveSearchModel(searchReq.getSchemaName())
            .forEach(model -> {
                if(OrgStructOrganizationFacade.MODEL_NAME.equals(model.getName())) {
                    authorizationService.secure(OrgStructPrivilegesHolder.ORGANIZATION_LOAD);
                } else if(OrganizationalPersonFacade.MODEL_NAME.equals(model.getName())) {
                    authorizationService.secure(OrgStructPrivilegesHolder.ORGANIZATIONAL_PERSON_SEARCH);
                }
            });
        
        return super.loadRoot(searchReq);
    }
    
}
