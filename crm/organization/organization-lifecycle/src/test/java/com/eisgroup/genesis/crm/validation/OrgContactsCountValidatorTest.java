/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.crm.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.eisgroup.genesis.factory.model.organizationcustomer.GenesisCrmCommunicationInfo;
import com.eisgroup.genesis.factory.model.organizationcustomer.GenesisCrmEmail;
import com.eisgroup.genesis.factory.model.organizationcustomer.GenesisCrmSocialNet;

public class OrgContactsCountValidatorTest {
    private OrgContactsCountValidator validator;

    @Mock
    private GenesisCrmCommunicationInfo communicationInfo;
    @Mock
    private OrgContactsCount contactsCount;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        validator = new OrgContactsCountValidator();
    }

    @Test
    public void initializeCorrectTest() {
        when(contactsCount.min()).thenReturn(1);
        when(contactsCount.max()).thenReturn(2);
        validator.initialize(contactsCount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializeIncorrectMinMaxTest() {
        when(contactsCount.min()).thenReturn(2);
        when(contactsCount.max()).thenReturn(1);
        validator.initialize(contactsCount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializeIncorrectMinTest() {
        when(contactsCount.max()).thenReturn(1);
        when(contactsCount.min()).thenReturn(-1);
        validator.initialize(contactsCount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializeIncorrectMaxTest() {
        when(contactsCount.max()).thenReturn(-1);
        when(contactsCount.min()).thenReturn(1);
        validator.initialize(contactsCount);
    }

    @Test
    public void isValidNullCommunicationInfoTest() {
        boolean actual = validator.isValid(null, null);
        assertTrue("CommunicationInfo is not null", actual);
    }

    @Test
    public void isValidZeroLengthTest() {
        boolean actual = validator.isValid(communicationInfo, null);
        assertTrue("Length is wrong", actual);
    }

    @Test
    public void isValidCorrectLengthTest() {
        when(contactsCount.min()).thenReturn(1);
        when(contactsCount.max()).thenReturn(4);
        validator.initialize(contactsCount);

        when(communicationInfo.getEmails()).thenReturn((List)Arrays.asList(
                Mockito.mock(GenesisCrmEmail.class),
                Mockito.mock(GenesisCrmEmail.class)
        ));
        when(communicationInfo.getSocialNets()).thenReturn((List)Collections.singletonList(
                Mockito.mock(GenesisCrmSocialNet.class)
        ));

        boolean actual = validator.isValid(communicationInfo, null);
        assertTrue("Length is wrong", actual);
    }

    @Test
    public void isValidIncorrectLengthTest() {
        when(communicationInfo.getEmails()).thenReturn((List)Collections.singletonList(
                Mockito.mock(GenesisCrmEmail.class)
        ));

        boolean actual = validator.isValid(communicationInfo, null);
        assertFalse("Length is correct", actual);
    }
}
