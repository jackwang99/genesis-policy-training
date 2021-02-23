/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.commands.crm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Collections;

import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.eisgroup.genesis.crm.validation.AddressCountValidator;
import com.eisgroup.genesis.factory.model.individualcustomer.GenesisCrmCommunicationInfo;
import com.eisgroup.genesis.factory.model.individualcustomer.IndividualCustomer;

public class AddressCountValidatorTest {

    @InjectMocks
    private AddressCountValidator addressCountValidator;

    @Mock
    private IndividualCustomer customer;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Mock
    private GenesisCrmCommunicationInfo genesisCrmCommunicationInfo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(customer.getCommunicationInfo()).thenReturn(genesisCrmCommunicationInfo);
    }

    @Test
    public void shouldPassValidationWhenCustomerIsNull(){
        //when
        boolean isValid = addressCountValidator.isValid(null, constraintValidatorContext);

        //then
        assertThat(isValid, is(true));
    }

    @Test
    public void shouldPassValidationWhenCustomerCommunicationInfoIsNull(){
        //given
        when(customer.getCommunicationInfo()).thenReturn(null);

        //when
        boolean isValid = addressCountValidator.isValid(customer, constraintValidatorContext);

        //then
        assertThat(isValid, is(true));
    }

    @Test
    public void shouldNotPassValidationWhenCustomerStateIsBlank(){
        //given
        when(customer.getState()).thenReturn(" ");
        when(genesisCrmCommunicationInfo.getAddresses()).thenReturn(Collections.emptyList());

        //when
        boolean isValid = addressCountValidator.isValid(customer, constraintValidatorContext);

        //then
        assertThat(isValid, is(false));
    }

    @Test
    public void shouldNotPassValidationWhenCustomerStateAreQualified(){
        //given
        when(customer.getState()).thenReturn("qualified");
        when(genesisCrmCommunicationInfo.getAddresses()).thenReturn(Collections.emptyList());

        //when
        boolean isValid = addressCountValidator.isValid(customer, constraintValidatorContext);

        //then
        assertThat(isValid, is(false));
    }

    @Test
    public void shouldPassValidationWhenCustomerStateAreNotQualified(){
        //given
        when(customer.getState()).thenReturn("Not qualified");

        //when
        boolean isValid = addressCountValidator.isValid(customer, constraintValidatorContext);

        //then
        assertThat(isValid, is(true));
    }

    @Test
    public void shouldNotPassValidationWhenCustomerAddressesAreEmpty(){
        //given
        when(genesisCrmCommunicationInfo.getAddresses()).thenReturn(Collections.emptyList());
        when(customer.getState()).thenReturn(null);

        //when
        boolean isValid = addressCountValidator.isValid(customer, constraintValidatorContext);

        //then
        assertThat(isValid, is(false));
    }
}
