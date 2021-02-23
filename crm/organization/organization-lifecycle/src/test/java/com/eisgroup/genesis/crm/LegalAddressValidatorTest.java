/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.crm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.eisgroup.genesis.crm.validation.LegalAddressValidator;
import com.eisgroup.genesis.factory.model.organizationcustomer.GenesisCrmAddress;
import com.eisgroup.genesis.factory.model.organizationcustomer.GenesisCrmCommunicationInfo;
import com.eisgroup.genesis.factory.model.organizationcustomer.OrganizationCustomer;

public class LegalAddressValidatorTest {

    @InjectMocks
    private LegalAddressValidator legalAddressValidator;

    @Mock
    private OrganizationCustomer organization;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Mock
    private GenesisCrmCommunicationInfo communicationInfo;

    @Mock
    private GenesisCrmAddress genesisCrmAddress;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(organization.getCommunicationInfo()).thenReturn(communicationInfo);
    }

    @Test
    public void shouldPassValidationWhenOrganizationIsNull(){
        //when
        boolean isValid = legalAddressValidator.isValid(null, constraintValidatorContext);

        //then
        assertThat(isValid, is(true));
    }

    @Test
    public void shouldPassValidationWhenOrganizationCommunicationInfoIsNull(){
        //given
        when(organization.getCommunicationInfo()).thenReturn(null);

        //when
        boolean isValid = legalAddressValidator.isValid(organization, constraintValidatorContext);

        //then
        assertThat(isValid, is(true));
    }

    @Test
    public void shouldPassValidationWhenOrganizationStateIsNotNull(){
        //given
        when(organization.getState()).thenReturn("Not Null");

        //when
        boolean isValid = legalAddressValidator.isValid(organization, constraintValidatorContext);

        //then
        assertThat(isValid, is(true));
    }

    @Test
    public void shouldNotPassValidationWhenOrganizationStateAreQualified(){
        //given
        when(organization.getState()).thenReturn("qualified");

        //when
        boolean isValid = legalAddressValidator.isValid(organization, constraintValidatorContext);

        //then
        assertThat(isValid, is(false));
    }

    @Test
    public void shouldNotPassValidationWhenOragnizationHaveNoAddresses(){
        //given
        when(organization.getState()).thenReturn(null);
        when(communicationInfo.getAddresses()).thenReturn(Collections.emptyList());

        //when
        boolean isValid = legalAddressValidator.isValid(organization, constraintValidatorContext);

        //then
        assertThat(isValid, is(false));
    }

    @Test
    public void shouldNotPassValidationWhenComunicationAdressesDoNotHaveWordLegal(){
        //given
        List<GenesisCrmAddress> addressList = new ArrayList();
        addressList.add(genesisCrmAddress);
        when(communicationInfo.getAddresses()).thenReturn((List)addressList);

        //when
        boolean isValid = legalAddressValidator.isValid(organization, constraintValidatorContext);

        //then
        assertThat(isValid, is(false));
    }
}
