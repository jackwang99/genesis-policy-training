/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.facade;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.access.annotation.Secured;

import com.eisgroup.genesis.facade.endpoint.command.CommandEndpoint;
import com.eisgroup.genesis.facade.endpoint.load.LoadCurrentEntityVersionEndpoint;
import com.eisgroup.genesis.facade.endpoint.load.LoadEntityByBusinessKeyRequest;
import com.eisgroup.genesis.facade.endpoint.load.LoadEntityEndpoint;
import com.eisgroup.genesis.facade.endpoint.load.LoadEntityPartRequest;
import com.eisgroup.genesis.facade.endpoint.load.LoadEntityRootRequest;
import com.eisgroup.genesis.facade.endpoint.load.LoadHistoryEndpoint;
import com.eisgroup.genesis.facade.endpoint.load.LoadSingleEntityRootRequest;
import com.eisgroup.genesis.facade.endpoint.load.dto.LoadHistoryResult;
import com.eisgroup.genesis.facade.endpoint.load.link.EntityLinkEndpoint;
import com.eisgroup.genesis.facade.endpoint.load.link.EntityLinkRequest;
import com.eisgroup.genesis.facade.endpoint.model.ModelFacade;
import com.eisgroup.genesis.facade.module.EndpointPackage;
import com.eisgroup.genesis.facade.module.FacadeModule;
import com.eisgroup.genesis.factory.modeling.types.Organization;
import com.eisgroup.genesis.factory.modeling.types.OrganizationalPerson;
import com.eisgroup.genesis.security.roles.OrgStructPrivilegesHolder;
import com.google.gson.JsonObject;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Organizational person facade configuration.
 * 
 * @author dlevchuk, adainelis
 * 
 */
public class OrganizationalPersonFacade implements FacadeModule {
    
    public static final String MODEL_NAME = "OrganizationalPerson";

    @Override
    public String getModelType() {
        return MODEL_NAME;
    }

    @Override
    public String getModelName() {
        return MODEL_NAME;
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

        endpoints.add(new LoadOrgPersonEndpoint());
        endpoints.add(new LoadOrgPersonHistoryEndpoint());
        endpoints.add(new LoadCurrentOrgPersonVersionEndpoint());
        endpoints.add(new LoadCurrentUserOrganizationalAssignmentsEndpoint());
        
        endpoints.add(new OrgPersonEntityLinkEndpoint());

        return endpoints;
    }
    
    public static class LoadOrgPersonEndpoint extends LoadEntityEndpoint<OrganizationalPerson> {
        @Override
        @Secured(OrgStructPrivilegesHolder.ORGANIZATIONAL_PERSON_LOAD)
        public Observable<JsonObject> loadParts(LoadEntityPartRequest partReq) {
            return super.loadParts(partReq);
        }
        @Override
        @Secured(OrgStructPrivilegesHolder.ORGANIZATIONAL_PERSON_LOAD)
        public Single<JsonObject> loadRoot(LoadEntityByBusinessKeyRequest readReq, Integer revisionNo) {
            return super.loadRoot(readReq, revisionNo);
        }
        @Override
        @Secured(OrgStructPrivilegesHolder.ORGANIZATIONAL_PERSON_LOAD)
        public Single<JsonObject> loadRoot(LoadSingleEntityRootRequest rootReq) {
            return super.loadRoot(rootReq);
        }
    }
    
    public static class LoadOrgPersonHistoryEndpoint extends LoadHistoryEndpoint<Organization> {
        @Override
        @Secured(OrgStructPrivilegesHolder.ORGANIZATIONAL_PERSON_LOAD)
        public Single<LoadHistoryResult> loadRoot(LoadEntityRootRequest rootReq) {
            return super.loadRoot(rootReq);
        }
        @Override
        @Secured(OrgStructPrivilegesHolder.ORGANIZATIONAL_PERSON_LOAD)
        public Single<LoadHistoryResult> loadRoot(LoadEntityByBusinessKeyRequest req, Integer limit, Integer offset) {
            return super.loadRoot(req, limit, offset);
        }
    }
    
    public static class LoadCurrentOrgPersonVersionEndpoint extends LoadCurrentEntityVersionEndpoint<Organization> {
        @Override
        @Secured(OrgStructPrivilegesHolder.ORGANIZATIONAL_PERSON_LOAD)
        public Single<JsonObject> loadRoot(LoadEntityByBusinessKeyRequest readReq, LocalDateTime onDate) {
            return super.loadRoot(readReq, onDate);
        }
        @Override
        @Secured(OrgStructPrivilegesHolder.ORGANIZATIONAL_PERSON_LOAD)
        public Single<JsonObject> loadRoot(LoadSingleEntityRootRequest rootReq, LocalDateTime onDate) {
            return super.loadRoot(rootReq, onDate);
        }
    }
    
    public static class OrgPersonEntityLinkEndpoint extends EntityLinkEndpoint {
        @Override
        @Secured(OrgStructPrivilegesHolder.ORGANIZATIONAL_PERSON_LOAD)
        public Observable<JsonObject> resolveAll(EntityLinkRequest req) {
            return super.resolveAll(req);
        }
    }

}
