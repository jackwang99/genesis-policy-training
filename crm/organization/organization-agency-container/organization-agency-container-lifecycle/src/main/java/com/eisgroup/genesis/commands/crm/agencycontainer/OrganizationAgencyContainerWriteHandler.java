/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.commands.crm.agencycontainer;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import com.eisgroup.genesis.commands.request.OrganizationAgencyContainerWriteRequest;
import com.eisgroup.genesis.factory.model.lifecycle.Description;
import com.eisgroup.genesis.factory.model.organizationagencycontainer.GenesisOrganizationAgencyContainer;
import com.eisgroup.genesis.exception.ErrorHolder;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.model.lifecycle.Modifying;
import com.eisgroup.genesis.factory.modeling.types.OrganizationAgencyContainer;
import com.eisgroup.genesis.factory.modeling.types.immutable.OrganizationCustomerBase;
import com.eisgroup.genesis.crm.validation.Paths;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

import io.reactivex.Observable;

@Modifying
@Description("oacwh001: Creates new organization agency container or updates existing.")
public class OrganizationAgencyContainerWriteHandler extends AbstractAgencyContainerWriteHandler<OrganizationAgencyContainerWriteRequest, OrganizationAgencyContainer, OrganizationCustomerBase> {
    
    private static final String AGENCY_CONTAINER_ADDRESSES = "addresses";
    
    @Override
    public Observable<ErrorHolder> validateAsync(final OrganizationAgencyContainerWriteRequest request, OrganizationAgencyContainer entity) {
        OrganizationAgencyContainer container = request.getEntity();
        return super.validateAsync(request, entity)
                .concatWith(validationService.checkEntityLinks(Collections.singletonList(container.getCustomer()), Paths.CUSTOMER));
    }
    
    @Override
    protected void initContainer(OrganizationAgencyContainer ac) {
        super.initContainer(ac);
        
        customerContactService.setContactsUpdatedDate(((GenesisOrganizationAgencyContainer) ac).getAddresses(), null, LocalDateTime.now());
    }
    
    @Override
    protected void copyLoadedEntityFields(OrganizationAgencyContainer loaded, OrganizationAgencyContainer current) {
        super.copyLoadedEntityFields(loaded, current);
        
        JsonObject acJson = current.toJson();
        JsonObject previousAcJson = loaded.toJson();

        GenesisOrganizationAgencyContainer ac = (GenesisOrganizationAgencyContainer) ModelInstanceFactory.createInstance(acJson);
        
        Set<UUID> updatedContactsKeys = null;
        if (!previousAcJson.entrySet().isEmpty()) {
            Set<String> contactFieldNames = Sets.newHashSet(AGENCY_CONTAINER_ADDRESSES);
            DomainModel model = modelResolver.resolveModel(DomainModel.class);
            updatedContactsKeys = customerContactService.getUpdatedContactsKeys(acJson, previousAcJson, contactFieldNames, model);
            
            GenesisOrganizationAgencyContainer pac = (GenesisOrganizationAgencyContainer) ModelInstanceFactory.createInstance(previousAcJson);
            ac.setCustomer(pac.getCustomer());            
        }
        
        customerContactService.setContactsUpdatedDate(ac.getAddresses(), updatedContactsKeys, LocalDateTime.now());
    }    
}
