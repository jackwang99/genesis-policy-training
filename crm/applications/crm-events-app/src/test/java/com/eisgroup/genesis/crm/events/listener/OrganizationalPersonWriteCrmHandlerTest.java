/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.events.listener;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;

import java.util.UUID;
import java.util.function.Predicate;

import com.eisgroup.genesis.model.external.ExternalModelRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.eisgroup.genesis.command.result.CommandFailure;
import com.eisgroup.genesis.commands.services.CrmValidationErrorDefinition;
import com.eisgroup.genesis.crm.commands.CrmCommands;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.json.IdentifiableEntity;
import com.eisgroup.genesis.factory.model.lifecycle.Command;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.eisgroup.genesis.factory.modeling.types.OrganizationalPerson;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.lifecycle.events.CommandExecutedEvent;
import com.eisgroup.genesis.lifecycle.events.StateTransition;
import com.eisgroup.genesis.orgstruct.commands.CommandNames;
import com.eisgroup.genesis.test.utils.JsonUtils;
import com.google.gson.JsonObject;

import io.reactivex.Single;

/**
 * 
 * @author Dmitry Andronchik
 * @since 10.3
 */
public class OrganizationalPersonWriteCrmHandlerTest {
    
    private static final String INDIVIDUALCUSTOMER_MODEL_NAME = "INDIVIDUALCUSTOMER";
    private static final String MODEL_VERSION = "1";
    
    private static final String INITED_CUSTOMER_IMAGE = "initedIndividualCustomer.json";
    
    @Mock
    private Command command;
    @Mock
    private StateTransition stateTransition;
    @Mock
    private CustomerCommandEventHandler customerCommandEventHandler;

    private OrganizationalPersonWriteCrmHandler handler;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        Predicate<String> filter = commandName -> CommandNames.ORGANIZATIONAL_PERSON_WRITE.getName().equals(commandName);

        ExternalModelRepository externalModelRepository = Mockito.mock(ExternalModelRepository.class);
        handler = new OrganizationalPersonWriteCrmHandler(INDIVIDUALCUSTOMER_MODEL_NAME, "1", filter, externalModelRepository, customerCommandEventHandler);
    }
    
    @Test
    public void testWrongEvent() {
        
        Customer customer = (Customer) loadEntity(INDIVIDUALCUSTOMER_MODEL_NAME, MODEL_VERSION, INITED_CUSTOMER_IMAGE);
     
        CommandExecutedEvent event = new CommandExecutedEvent(command, stateTransition, customer);
        
        handler.handle(event)
                .test()
                .assertComplete();
        
        verify(customerCommandEventHandler, never()).executeCommand(Mockito.eq(CrmCommands.INIT_CUSTOMER), Mockito.eq(INDIVIDUALCUSTOMER_MODEL_NAME), Mockito.any());
    }
    
    @Test
    public void organizationalPersonNotFirstVersion() {
        
        OrganizationalPerson orgPerson = mock(OrganizationalPerson.class);
        CommandExecutedEvent event = new CommandExecutedEvent(command, stateTransition, orgPerson);
        
        when(orgPerson.getKey()).thenReturn(new RootEntityKey(UUID.randomUUID(), 2));
        
        handler.handle(event)
                .test()
                .assertComplete();
        
        verify(customerCommandEventHandler, never()).executeCommand(Mockito.eq(CrmCommands.INIT_CUSTOMER), Mockito.eq(INDIVIDUALCUSTOMER_MODEL_NAME), Mockito.any());        
    }
    
    @Test
    public void customerInitializationError() {
        
        OrganizationalPerson orgPerson = mock(OrganizationalPerson.class);
        CommandExecutedEvent event = new CommandExecutedEvent(command, stateTransition, orgPerson);
        
        when(orgPerson.getKey()).thenReturn(new RootEntityKey(UUID.randomUUID(), 1));
        when(customerCommandEventHandler.executeCommand(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Single.just(new CommandFailure(CrmValidationErrorDefinition.CONSTRAINT_VIOLATION.builder().build())));
        
        handler.handle(event)
                .test()
                .assertComplete();
        
        verify(customerCommandEventHandler, times(1)).executeCommand(Mockito.eq(CrmCommands.INIT_CUSTOMER), Mockito.eq(INDIVIDUALCUSTOMER_MODEL_NAME), Mockito.any());
        verify(customerCommandEventHandler, never()).executeCommand(Mockito.eq(CrmCommands.WRITE_CUSTOMER), Mockito.eq(INDIVIDUALCUSTOMER_MODEL_NAME), Mockito.any());
    }
    
    private IdentifiableEntity loadEntity(String modelName, String modelVersion, String image) {
        JsonObject entityJson = JsonUtils.load(image);
        return (IdentifiableEntity) ModelInstanceFactory.createInstance(modelName, modelVersion, entityJson);
    }    
}
