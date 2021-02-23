/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.crm.validation;

import com.eisgroup.genesis.factory.model.organizationcustomer.GenesisCrmAddress;
import com.eisgroup.genesis.factory.model.organizationcustomer.GenesisLocation;
import com.eisgroup.genesis.factory.model.organizationcustomer.GenesisCrmCommunicationInfo;
import com.eisgroup.genesis.factory.model.organizationcustomer.OrganizationCustomer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static com.eisgroup.genesis.crm.validation.LegalAddressValidator.LEGAL_ADDRESS_TYPE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class LegalAddressValidatorTest {
    private LegalAddressValidator validator;

    @Mock
    private OrganizationCustomer organizationCustomer;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        validator = new LegalAddressValidator();
    }

    @Test
    public void isValidWithNullOrganizationCustomer() {
        boolean actual = validator.isValid(null, null);
        assertTrue("OrganizationCustomer is not null", actual);
    }

    @Test
    public void isValidWithNullCommunicationInfo() {
        boolean actual = validator.isValid(organizationCustomer, null);
        assertTrue("CommunicationInfo is not null", actual);
    }

    @Test
    public void isValidWithNotQualifiedState() {
        when(organizationCustomer.getCommunicationInfo()).thenReturn(Mockito.mock(GenesisCrmCommunicationInfo.class));
        when(organizationCustomer.getState()).thenReturn("other");
        boolean actual = validator.isValid(organizationCustomer, null);
        assertTrue("State is not qualified", actual);
    }

    @Test
    public void isValidWithoutAddresses() {
        GenesisCrmCommunicationInfo communicationInfo = Mockito.mock(GenesisCrmCommunicationInfo.class);
        when(organizationCustomer.getCommunicationInfo()).thenReturn(communicationInfo);
        boolean actual = validator.isValid(organizationCustomer, null);
        assertFalse("CommunicationInfo contains addresses", actual);
    }

    @Test
    public void isValidWithAddresses() {
        GenesisCrmCommunicationInfo communicationInfo = Mockito.mock(GenesisCrmCommunicationInfo.class);
        when(organizationCustomer.getCommunicationInfo()).thenReturn(communicationInfo);
        GenesisCrmAddress address = Mockito.mock(GenesisCrmAddress.class);
        when(communicationInfo.getAddresses()).thenReturn((List)Collections.singletonList(address));
        GenesisLocation location = Mockito.mock(GenesisLocation.class);
        when(address.getLocation()).thenReturn(location);
        when(location.getAddressType()).thenReturn(LEGAL_ADDRESS_TYPE);

        boolean actual = validator.isValid(organizationCustomer, null);
        assertTrue("Addresses are not correct", actual);

        when(address.getLocation().getAddressType()).thenReturn("Type");

        actual = validator.isValid(organizationCustomer, null);
        assertFalse("Addresses are correct", actual);
    }
}
