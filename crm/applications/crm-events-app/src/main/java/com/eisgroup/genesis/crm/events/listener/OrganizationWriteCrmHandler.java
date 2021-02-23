/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.events.listener;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.eisgroup.genesis.model.external.ExternalModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import com.eisgroup.genesis.command.result.CommandFailure;
import com.eisgroup.genesis.command.result.CommandResult;
import com.eisgroup.genesis.command.result.CommandSuccess;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.modeling.types.CrmAddress;
import com.eisgroup.genesis.factory.modeling.types.CrmBusinessDetails;
import com.eisgroup.genesis.factory.modeling.types.LegalEntityBase;
import com.eisgroup.genesis.factory.modeling.types.LocationBase;
import com.eisgroup.genesis.factory.modeling.types.OrganizationCustomerBase;
import com.eisgroup.genesis.factory.modeling.types.immutable.CommunicationInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.LegalEntity;
import com.eisgroup.genesis.factory.modeling.types.immutable.Organization;
import com.eisgroup.genesis.factory.modeling.types.immutable.OrganizationDetails;
import com.eisgroup.genesis.lifecycle.events.CommandExecutedEvent;
import com.google.gson.JsonObject;

import io.reactivex.Completable;

/**
 * 
 * @author Dmitry Andronchik
 * @since 9.13
 */
public class OrganizationWriteCrmHandler extends BaseOrgStuctureWriteCrmHandler {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationWriteCrmHandler.class);
    
    private final String businessType;
    private final String naicsCode;
    
    public OrganizationWriteCrmHandler(String customerModelName, String customerModelVersion, String businessType, String naicsCode, Predicate<String> filterFunction,
                                       ExternalModelRepository externalModelRepo, CustomerCommandEventHandler customerCommandEventHandler) {
        super(customerModelName, customerModelVersion, filterFunction, externalModelRepo, customerCommandEventHandler);
        this.businessType = businessType;
        this.naicsCode = naicsCode;
    }

    @Override
    public Completable handle(CommandExecutedEvent message) {
        return Optional.ofNullable(message.getOutput())
            .filter(entity -> entity instanceof Organization)
            .map(entity -> doHandle((Organization) entity, message))
            .orElse(Completable.complete());
    }

    protected Completable doHandle(Organization entity, CommandExecutedEvent message) {
        return Completable.fromAction(() -> {
            if(entity.getKey().getRevisionNo() == 1) {
                CommandResult initCommandResult = initNewCustomer().blockingGet();
                
                if (initCommandResult.isFailure()) {
                    logger.warn("Can't init customer: " + ((CommandFailure) initCommandResult).getData());
                } else {                
                    OrganizationCustomerBase customer = initOrganizationCustomer(((CommandSuccess) initCommandResult).getData().getAsJsonObject(), entity);

                    sendCustomerWriteCommand(customer);
                }
            }            
        });
    }
    
    protected OrganizationCustomerBase initOrganizationCustomer(JsonObject organizationCustomerJson, Organization entity) {
        OrganizationCustomerBase customer = (OrganizationCustomerBase) ModelInstanceFactory.createInstance(organizationCustomerJson);
        
        CrmBusinessDetails details = (CrmBusinessDetails) ModelInstanceFactory.createInstanceByBusinessType(customerModelName, customerModelVersion, CrmBusinessDetails.class.getSimpleName());
        customer.setDetails(details);
        details.setNaicsCode(naicsCode);
        
        details.setBusinessName(entity.getName());
        details.setBusinessType(businessType);

        OrganizationDetails orgDetails = entity.getDetails();
        
        LegalEntityBase legalEntity = (LegalEntityBase) ModelInstanceFactory.createInstanceByBusinessType(customerModelName, customerModelVersion, LegalEntity.class.getSimpleName());
        BeanUtils.copyProperties(orgDetails.getLegalEntityBase(), legalEntity, "_type", "_key");
        details.setLegalEntity(legalEntity);
        
        CommunicationInfo orgCommunicationInfo = orgDetails.getLegalEntityBase().getCommunicationInfo();
        customer.setCommunicationInfo(initCommunicationInfo(orgCommunicationInfo));
        
        if(!CollectionUtils.isEmpty(orgDetails.getOrganizationAddress())) {
            List<? extends CrmAddress> crmAddresses = orgDetails.getOrganizationAddress()
                        .stream()
                        .map(orgAddress -> {
                            CrmAddress crmAddress = (CrmAddress) ModelInstanceFactory.createInstanceByBusinessType(customerModelName, customerModelVersion, CrmAddress.class.getSimpleName());
                            LocationBase location = (LocationBase) ModelInstanceFactory.createInstanceByBusinessType(customerModelName, customerModelVersion, LocationBase.class.getSimpleName()); 
                            BeanUtils.copyProperties(orgAddress.getAddress(), location, "_type", "_key");
                            crmAddress.setLocation(location);                                    
                            return crmAddress;
                        })
                        .collect(Collectors.toList());
            customer.getCommunicationInfo().setAddresses(crmAddresses);
        }
        
        return customer;
    }
}
