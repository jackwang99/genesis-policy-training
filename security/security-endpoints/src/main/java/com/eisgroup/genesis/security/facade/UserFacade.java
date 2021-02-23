/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.security.facade;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.access.annotation.Secured;

import com.eisgroup.genesis.facade.endpoint.command.CommandEndpoint;
import com.eisgroup.genesis.facade.endpoint.load.LoadEntityEndpoint;
import com.eisgroup.genesis.facade.endpoint.load.LoadEntityPartRequest;
import com.eisgroup.genesis.facade.endpoint.load.LoadSingleEntityRootRequest;
import com.eisgroup.genesis.facade.endpoint.load.link.EntityLinkEndpoint;
import com.eisgroup.genesis.facade.endpoint.load.link.EntityLinkRequest;
import com.eisgroup.genesis.facade.endpoint.model.ModelFacade;
import com.eisgroup.genesis.facade.module.EndpointPackage;
import com.eisgroup.genesis.facade.module.FacadeModule;
import com.eisgroup.genesis.factory.modeling.types.immutable.User;
import com.google.gson.JsonObject;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Initial User Facade
 *
 * @author alizdenis
 */
public class UserFacade implements FacadeModule {

    @Override
    public String getModelType() {
        return "UserDomain";
    }

    @Override
    public String getModelName() {
        return "User";
    }

    @Override
    public int getFacadeVersion() {
        return 1;
    }

    @Override
    public Collection<EndpointPackage> getEndpoints() {
        Set<EndpointPackage> endpoints = new HashSet<>();
        endpoints.add(new ModelFacade());
        endpoints.add(new CommandEndpoint());
        endpoints.add(new UserLoadEntityEndpoint());
        endpoints.add(new SecuredEntityLinkEndpoint());
        return endpoints;
    }
    
    /**
     * Secured entity link resolution endpoint
     */
    public static class SecuredEntityLinkEndpoint extends EntityLinkEndpoint {
        @Override
        @Secured(Privileges.USER_LOAD)
        public Observable<JsonObject> resolveAll(EntityLinkRequest req) {
            return super.resolveAll(req);
        }
    }
    
    /**
     * Secured load endpoint for User
     */
    public class UserLoadEntityEndpoint extends LoadEntityEndpoint<User> {
        @Override
        @Secured(Privileges.USER_LOAD)
        public Single<JsonObject> loadRoot(LoadSingleEntityRootRequest rootReq) {
            return super.loadRoot(rootReq);
        }
        @Override
        @Secured(Privileges.USER_LOAD)
        public Observable<JsonObject> loadParts(LoadEntityPartRequest partReq) {
            return super.loadParts(partReq);
        }
    }

}
