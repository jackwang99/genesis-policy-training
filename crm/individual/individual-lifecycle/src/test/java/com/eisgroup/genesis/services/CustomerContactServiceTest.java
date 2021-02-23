/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.services;

import com.eisgroup.genesis.commands.services.CustomerContactService;
import com.eisgroup.genesis.comparison.impl.config.ComparisonConfig;
import com.eisgroup.genesis.crm.services.customer.CustomerContactServiceImpl;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.model.individualcustomer.impl.IndividualCustomerImpl;
import com.eisgroup.genesis.factory.modeling.types.*;
import com.eisgroup.genesis.factory.repository.key.KeyTraversalUtil;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.spring.config.SpringCommonsConfig;
import com.eisgroup.genesis.test.IntegrationTests;
import com.eisgroup.genesis.test.utils.JsonUtils;
import com.eisgroup.genesis.util.GsonUtil;
import com.google.common.collect.Sets;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * 
 * @author Dmitry Andronchik
 * @since 9.14
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTests.class)
@ContextConfiguration(classes = { ComparisonConfig.class,
                                  SpringCommonsConfig.class,
                                  CustomerContactServiceTest.TestConfig.class })
public class CustomerContactServiceTest {
    
    private static final String INDIVIDUAL_CUSTOMER_2_IMAGE = "individualCustomer2.json";
    private static final String INDIVIDUAL_CUSTOMER_3_IMAGE = "individualCustomer3.json";
    private static final String INDIVIDUAL_CUSTOMER_BUSINESS_IMAGE = "individualCustomerBusinessEntities.json";
    
    private static final String ADDRESSES_FIELD = "communicationInfo//addresses";
    private static final String EMAILS_FIELD = "communicationInfo//emails";
    private static final String PHONES_FIELD = "communicationInfo//phones";
    
    @Autowired
    private CustomerContactService customerContactService;
    @Autowired
    private ModelResolver modelResolver;    

    @Test
    public void testSetCommInfoContactsUpdatedDate() {
        
        Customer customer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_2_IMAGE));
        LocalDateTime updatedDate = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        
        customerContactService.setContactsUpdatedDate(customer.getCommunicationInfo().getPhones(), Sets.newHashSet(UUID.fromString("183c9010-4da3-4fb1-982a-1723df9f6000")), updatedDate);
        assertThat(customer.getCommunicationInfo().getPhones(), everyItem(hasProperty("updatedOn", equalTo(updatedDate))));
        
        customerContactService.setContactsUpdatedDate(customer.getCommunicationInfo().getEmails(), Sets.newHashSet(UUID.fromString("183c9010-4da3-4fb1-982a-1723df007000")), updatedDate);
        assertThat(customer.getCommunicationInfo().getEmails(), everyItem(hasProperty("updatedOn", equalTo(updatedDate))));        
    }

    @Test
    public void testSetEmploymentCommInfoContactsUpdatedDate() {

        IndividualCustomerImpl customer = (IndividualCustomerImpl) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_BUSINESS_IMAGE));
        LocalDateTime updatedDate = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        CrmCommunicationInfo communicationInfo = customer.getEmploymentDetails().iterator().next().getCommunicationInfo();
        customerContactService.setContactsUpdatedDate(communicationInfo.getAddresses(), Sets.newHashSet(UUID.fromString("183c9010-4da3-4fb1-982a-1723df007000")), updatedDate);
        assertThat(communicationInfo.getAddresses(), everyItem(hasProperty("updatedOn", equalTo(updatedDate))));

        customerContactService.setContactsUpdatedDate(communicationInfo.getPhones(), Sets.newHashSet(UUID.fromString("283c9010-4da3-4fb1-982a-1723df9f6000")), updatedDate);
        assertThat(communicationInfo.getPhones(), everyItem(hasProperty("updatedOn", equalTo(updatedDate))));
    }
    
    @Test
    public void testGetUpdatedContactsKeysWithoutChanges() {
        
        Customer customer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_3_IMAGE));
        Customer previousCustomer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_3_IMAGE));        

        DomainModel model = modelResolver.resolveModel(DomainModel.class);
        KeyTraversalUtil.traverseRoot(customer.toJson(), model);        
        Set<UUID> keys = customerContactService.getUpdatedContactsKeys(customer.toJson(), previousCustomer.toJson(), Sets.newHashSet(ADDRESSES_FIELD), model);
        
        assertThat(keys, Matchers.empty());
    }    
    
    @Test
    public void testGetEmploymentUpdatedContactsKeysChangeAddressField() {

        IndividualCustomerImpl previousCustomer = (IndividualCustomerImpl) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_BUSINESS_IMAGE));

        DomainModel model = modelResolver.resolveModel(DomainModel.class);
        KeyTraversalUtil.traverseRoot(previousCustomer.toJson(), model);
        IndividualCustomerImpl customer = (IndividualCustomerImpl) ModelInstanceFactory.createInstance(GsonUtil.copy(previousCustomer.toJson()));

        CrmCommunicationInfo communicationInfo = customer.getEmploymentDetails().iterator().next().getCommunicationInfo();
        CrmAddress address = (CrmAddress) communicationInfo.getAddresses().iterator().next();
        address.setComment("na dvore trava na trave drova");

        Set<UUID> keys = customerContactService.getUpdatedContactsKeys(customer.toJson(), previousCustomer.toJson(), Sets.newHashSet(ADDRESSES_FIELD), model);

        assertThat(keys.size(), equalTo(1));
        assertThat(keys, Matchers.everyItem(equalTo(UUID.fromString("183c9010-4da3-4fb1-982a-1723df007000"))));
    }

    @Test
    public void testGetUpdatedContactsKeysChangeAddressField() {

        Customer customer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_3_IMAGE));
        Customer previousCustomer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_3_IMAGE));

        CrmCommunicationInfo communicationInfo = customer.getCommunicationInfo();
        CrmAddress address = (CrmAddress) communicationInfo.getAddresses().iterator().next();
        address.setComment("HO-HO");

        DomainModel model = modelResolver.resolveModel(DomainModel.class);
        KeyTraversalUtil.traverseRoot(customer.toJson(), model);
        Set<UUID> keys = customerContactService.getUpdatedContactsKeys(customer.toJson(), previousCustomer.toJson(), Sets.newHashSet(ADDRESSES_FIELD), model);

        assertThat(keys.size(), equalTo(1));
        assertThat(keys, Matchers.everyItem(equalTo(UUID.fromString("153d27f8-eff1-40ee-9f17-815d5cbcca7b"))));
    }
    
    @Test
    public void testGetUpdatedContactsKeysChangeAddressSubEntity() {
        
        Customer customer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_3_IMAGE));
        Customer previousCustomer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_3_IMAGE));        

        CrmCommunicationInfo communicationInfo = customer.getCommunicationInfo();
        CrmAddress address = (CrmAddress) communicationInfo.getAddresses().iterator().next();
        address.getLocation().setAddressLine1("new address line 1");
        
        DomainModel model = modelResolver.resolveModel(DomainModel.class);
        KeyTraversalUtil.traverseRoot(customer.toJson(), model);        
        Set<UUID> keys = customerContactService.getUpdatedContactsKeys(customer.toJson(), previousCustomer.toJson(), Sets.newHashSet(ADDRESSES_FIELD), model);

        assertThat(keys.size(), equalTo(1));
        assertThat(keys, Matchers.everyItem(equalTo(UUID.fromString("153d27f8-eff1-40ee-9f17-815d5cbcca7b"))));
    }
    
    @Test
    public void testGetUpdatedContactsKeysRemoveAddressField() {
        
        Customer customer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_3_IMAGE));
        Customer previousCustomer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_3_IMAGE));        

        CrmCommunicationInfo communicationInfo = customer.getCommunicationInfo();
        CrmAddress address = (CrmAddress) communicationInfo.getAddresses().iterator().next();
        address.toJson().remove("subdivision");
        
        DomainModel model = modelResolver.resolveModel(DomainModel.class);
        KeyTraversalUtil.traverseRoot(customer.toJson(), model);        
        Set<UUID> keys = customerContactService.getUpdatedContactsKeys(customer.toJson(), previousCustomer.toJson(), Sets.newHashSet(ADDRESSES_FIELD), model);
        
        assertThat(keys.size(), equalTo(1));
        assertThat(keys, Matchers.everyItem(equalTo(UUID.fromString("153d27f8-eff1-40ee-9f17-815d5cbcca7b"))));
    }

    @Test
    public void testGetEmploymentUpdatedContactsKeysRemoveAddressField() {

        IndividualCustomerImpl previousCustomer = (IndividualCustomerImpl) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_BUSINESS_IMAGE));

        DomainModel model = modelResolver.resolveModel(DomainModel.class);
        KeyTraversalUtil.traverseRoot(previousCustomer.toJson(), model);
        IndividualCustomerImpl customer = (IndividualCustomerImpl) ModelInstanceFactory.createInstance(GsonUtil.copy(previousCustomer.toJson()));

        CrmCommunicationInfo communicationInfo = customer.getEmploymentDetails().iterator().next().getCommunicationInfo();
        CrmAddress address = (CrmAddress) communicationInfo.getAddresses().iterator().next();
        address.getLocation().toJson().remove("addressLine3");

        Set<UUID> keys = customerContactService.getUpdatedContactsKeys(customer.toJson(), previousCustomer.toJson(), Sets.newHashSet(ADDRESSES_FIELD), model);

        assertThat(keys.size(), equalTo(1));
        assertThat(keys, Matchers.everyItem(equalTo(UUID.fromString("183c9010-4da3-4fb1-982a-1723df007000"))));
    }
    
    @Test
    public void testGetUpdatedContactsKeysAddEmailToExistingCustomer() {
        
        Customer customer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_3_IMAGE));
        Customer previousCustomer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_3_IMAGE));        

        CrmCommunicationInfo communicationInfo = customer.getCommunicationInfo();
        
        CrmEmail email = (CrmEmail) ModelInstanceFactory.createInstanceByBusinessType("INDIVIDUALCUSTOMER", "1", "CrmEmail");
        email.setValue("new@gmail.com");
        communicationInfo.getEmails().add(email);
        
        DomainModel model = modelResolver.resolveModel(DomainModel.class);
        KeyTraversalUtil.traverseRoot(customer.toJson(), model);        
        Set<UUID> keys = customerContactService.getUpdatedContactsKeys(customer.toJson(), previousCustomer.toJson(), Sets.newHashSet(EMAILS_FIELD), model);
        
        assertThat(keys.size(), equalTo(1));
        assertThat(keys, Matchers.everyItem(equalTo(email.getKey().getId())));        
    }

    @Test
    public void testGetEmploymentUpdatedContactsKeysAddAddressToExistingCustomer() {

        IndividualCustomerImpl previousCustomer = (IndividualCustomerImpl) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_BUSINESS_IMAGE));

        DomainModel model = modelResolver.resolveModel(DomainModel.class);
        KeyTraversalUtil.traverseRoot(previousCustomer.toJson(), model);
        IndividualCustomerImpl customer = (IndividualCustomerImpl) ModelInstanceFactory.createInstance(GsonUtil.copy(previousCustomer.toJson()));

        CrmCommunicationInfo communicationInfo = customer.getEmploymentDetails().iterator().next().getCommunicationInfo();

        CrmAddress address = (CrmAddress) ModelInstanceFactory.createInstanceByBusinessType("INDIVIDUALCUSTOMER", "1", CrmAddress.NAME);
        address.setLocation((LocationBase) ModelInstanceFactory.createInstanceByBusinessType("INDIVIDUALCUSTOMER", "1", LocationBase.NAME));
        address.getLocation().setAddressLine3("Pushkina-Kolotushkina Street");
        communicationInfo.getAddresses().add(address);

        KeyTraversalUtil.traverseRoot(customer.toJson(), model);
        Set<UUID> keys = customerContactService.getUpdatedContactsKeys(customer.toJson(), previousCustomer.toJson(), Sets.newHashSet(ADDRESSES_FIELD), model);

        assertThat(keys.size(), equalTo(1));
        assertThat(keys, Matchers.everyItem(equalTo(address.getKey().getId())));
    }
    
    @Test
    public void testUpdateCustomerContactsUpdatedDateAddEmailToExistingCustomer() {

        Customer customer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_3_IMAGE));
        Customer previousCustomer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_3_IMAGE));

        CrmCommunicationInfo communicationInfo = customer.getCommunicationInfo();
        CrmEmail existingEmail = (CrmEmail) communicationInfo.getEmails().iterator().next();
        LocalDateTime existingEmailUpdateOn = existingEmail.getUpdatedOn();

        CrmEmail email = (CrmEmail) ModelInstanceFactory.createInstanceByBusinessType("INDIVIDUALCUSTOMER", "1", "CrmEmail");
        email.setValue("new@gmail.com");
        communicationInfo.getEmails().add(email);        
        
        DomainModel model = modelResolver.resolveModel(DomainModel.class);
        KeyTraversalUtil.traverseRoot(customer.toJson(), model);
        LocalDateTime updatedOn = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        customerContactService.updateCustomerContactsUpdatedDate(customer, previousCustomer.toJson(), updatedOn, model);
        
        assertThat(email.getUpdatedOn(), equalTo(updatedOn));
        assertThat(existingEmail.getUpdatedOn(), equalTo(existingEmailUpdateOn));        
    }

    @Test
    public void testUpdateEmploymentContactsUpdatedDateAddAddressToExistingCustomer() {

        IndividualCustomerImpl previousCustomer = (IndividualCustomerImpl) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_BUSINESS_IMAGE));

        DomainModel model = modelResolver.resolveModel(DomainModel.class);
        KeyTraversalUtil.traverseRoot(previousCustomer.toJson(), model);
        IndividualCustomerImpl customer = (IndividualCustomerImpl) ModelInstanceFactory.createInstance(GsonUtil.copy(previousCustomer.toJson()));

        CrmCommunicationInfo communicationInfo = customer.getEmploymentDetails().iterator().next().getCommunicationInfo();

        CrmAddress existingAddress = (CrmAddress) communicationInfo.getAddresses().iterator().next();
        LocalDateTime existingAddressUpdateOn = existingAddress.getUpdatedOn();

        CrmAddress address = (CrmAddress) ModelInstanceFactory.createInstanceByBusinessType("INDIVIDUALCUSTOMER", "1", CrmAddress.NAME);
        address.setLocation((LocationBase) ModelInstanceFactory.createInstanceByBusinessType("INDIVIDUALCUSTOMER", "1", LocationBase.NAME));        
        address.getLocation().setAddressLine3("Petrovich-69 Street");
        communicationInfo.getAddresses().add(address);

        KeyTraversalUtil.traverseRoot(customer.toJson(), model);
        LocalDateTime updatedOn = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        customerContactService.updateCustomerContactsUpdatedDate(customer, previousCustomer.toJson(), updatedOn, model);

        assertThat(address.getUpdatedOn(), equalTo(updatedOn));
        assertThat(existingAddress.getUpdatedOn(), equalTo(existingAddressUpdateOn));
    }
    
    @Test
    // GENESIS-10596
    public void testGetUpdatedContactsKeysUpdatePhonePreferredTimesToContactField() {
        
        Customer customer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_3_IMAGE));
        Customer previousCustomer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(INDIVIDUAL_CUSTOMER_3_IMAGE));        

        CrmCommunicationInfo communicationInfo = customer.getCommunicationInfo();
        CrmPhone phone = (CrmPhone) communicationInfo.getPhones().iterator().next();
        phone.getPreferredTimesToContact().add("EVENING");
        
        DomainModel model = modelResolver.resolveModel(DomainModel.class);
        KeyTraversalUtil.traverseRoot(customer.toJson(), model);        
        Set<UUID> keys = customerContactService.getUpdatedContactsKeys(customer.toJson(), previousCustomer.toJson(), Sets.newHashSet(PHONES_FIELD), model);
        
        assertThat(keys.size(), equalTo(1));
        assertThat(keys, Matchers.everyItem(equalTo(UUID.fromString("a6958cc7-f53f-4fe5-8100-422bfff22847"))));
    }    
    
    static class TestConfig {

        @Bean
        public CustomerContactService customerContactService() {
            return new CustomerContactServiceImpl();
        }
        
        @Bean
        public ModelResolver modelResolver() {
            return new ModelResolver("INDIVIDUALCUSTOMER", "1");
        }        
    }    

}
