/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.events.listener;

import java.util.Optional;
import java.util.function.Predicate;

import com.eisgroup.genesis.model.external.ExternalModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.eisgroup.genesis.command.result.CommandFailure;
import com.eisgroup.genesis.command.result.CommandResult;
import com.eisgroup.genesis.command.result.CommandSuccess;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.modeling.types.IndividualCustomerBase;
import com.eisgroup.genesis.factory.modeling.types.IndividualDetails;
import com.eisgroup.genesis.factory.modeling.types.PersonBase;
import com.eisgroup.genesis.factory.modeling.types.immutable.CommunicationInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.OrganizationalPerson;
import com.eisgroup.genesis.lifecycle.events.CommandExecutedEvent;
import com.google.gson.JsonObject;

import io.reactivex.Completable;

/**
 * 
 * @author Dmitry Andronchik
 * @since 9.13
 */
public class OrganizationalPersonWriteCrmHandler extends BaseOrgStuctureWriteCrmHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(OrganizationalPersonWriteCrmHandler.class);
    
    public OrganizationalPersonWriteCrmHandler(String customerModelName, String customerModelVersion, Predicate<String> filterFunction,
                       ExternalModelRepository externalModelRepository, CustomerCommandEventHandler customerCommandEventHandler) {
        super(customerModelName, customerModelVersion, filterFunction, externalModelRepository, customerCommandEventHandler);
    }

    @Override
    public Completable handle(CommandExecutedEvent message) {
        return Optional.ofNullable(message.getOutput())
            .filter(entity -> entity instanceof OrganizationalPerson)
            .map(entity -> doHandle((OrganizationalPerson) entity, message))
            .orElse(Completable.complete());
    }

    protected Completable doHandle(OrganizationalPerson entity, CommandExecutedEvent message) {
        return Completable.fromAction(() -> {
            if(entity.getKey().getRevisionNo() == 1) {
                CommandResult initCommandResult = initNewCustomer().blockingGet();
                       
                if (initCommandResult.isFailure()) {
                    logger.warn("Can't init customer: " + ((CommandFailure) initCommandResult).getData());
                } else {
                    IndividualCustomerBase customer = initIndividualCustomer(((CommandSuccess) initCommandResult).getData().getAsJsonObject(), entity);
                    sendCustomerWriteCommand(customer);                            
                }
            }
        });
    }
    
    protected IndividualCustomerBase initIndividualCustomer(JsonObject individualCustomerJson, OrganizationalPerson entity) {
        
        IndividualCustomerBase customer = (IndividualCustomerBase) ModelInstanceFactory.createInstance(individualCustomerJson);
        
        IndividualDetails details = (IndividualDetails) ModelInstanceFactory.createInstanceByBusinessType(customerModelName, customerModelVersion, IndividualDetails.class.getSimpleName());
        customer.setDetails(details);
        
        PersonBase person = (PersonBase) ModelInstanceFactory.createInstanceByBusinessType(customerModelName, customerModelVersion, PersonBase.class.getSimpleName());
        BeanUtils.copyProperties(entity.getPersonInfo(), person, "_type", "_key");
        details.setPerson(person);

        CommunicationInfo organizationPersonCommunicationInfo = entity.getPersonInfo().getCommunicationInfo();
        customer.setCommunicationInfo(initCommunicationInfo(organizationPersonCommunicationInfo));
        
        return customer;
    }
}
