/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.conversion;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import com.eisgroup.genesis.conversion.domain.ConversionReaderModel;
import com.eisgroup.genesis.conversion.response.ConversionActionStatus;
import com.eisgroup.genesis.conversion.response.ConversionResponse;
import com.eisgroup.genesis.conversion.response.ConversionStatusMessage;
import com.eisgroup.genesis.conversion.services.ConversionSerializationService;
import com.eisgroup.genesis.factory.model.customeraccount.GenesisCustomerAccount;
import com.eisgroup.genesis.factory.model.individualcustomer.IndividualCustomer;
import com.eisgroup.genesis.factory.model.opportunity.GenesisOpportunity;
import com.eisgroup.genesis.factory.model.opportunity.GenesisUserOwner;
import com.eisgroup.genesis.factory.model.opportunity.GenesisWorkQueue;
import com.eisgroup.genesis.factory.model.organizationcustomer.OrganizationCustomer;
import com.eisgroup.genesis.factory.modeling.types.CrmCommunicationInfo;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.eisgroup.genesis.factory.modeling.types.IndividualAgencyContainer;
import com.eisgroup.genesis.factory.modeling.types.Opportunity;
import com.eisgroup.genesis.factory.modeling.types.OrganizationAgencyContainer;
import com.eisgroup.genesis.factory.modeling.types.ProductOwned;
import com.eisgroup.genesis.factory.modeling.types.immutable.CrmAddress;
import com.eisgroup.genesis.factory.modeling.types.immutable.CrmPhone;
import com.eisgroup.genesis.factory.modeling.types.immutable.EntityAssociation;
import com.eisgroup.genesis.factory.modeling.types.immutable.GroupInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;
import com.eisgroup.genesis.json.link.EntityLink;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/conversion-serialization-beans.xml",
        "/META-INF/spring/conversion-crm-serialization-beans.xml", "/META-INF/spring/conversion-crm-bindings-beans.xml",
        "/META-INF/spring/conversion-common-beans.xml", "/META-INF/spring/conversion-bindings-beans.xml" })
public class SerializationCustomerAccountTest {

    @Resource(name = "conversionCustomerAccountSerializationService")
    private ConversionSerializationService conversionSerializationService;

    @Test
    public void generateSchema() {

        try {

            String schema = conversionSerializationService.generateSchema();
            System.out.println(schema);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void unmarshal() {

        ConversionResponse conversionResponse = new ConversionResponse(null, null, null, null);

        try {

            FileInputStream fileInputStream = new FileInputStream(
                    this.getClass().getClassLoader().getResource("customerAccount.xml").getFile());

            String sourceFileBody = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8.name());
            System.out.println("Validating XML against the schema");

            ConversionReaderModel conversionReaderModel = (ConversionReaderModel) conversionSerializationService
                    .deserializeEntity(sourceFileBody, conversionResponse);

            List<RootEntity> rootEntities = conversionReaderModel.getRootEntities();

            GenesisCustomerAccount account = (GenesisCustomerAccount) rootEntities.get(0);
            Assert.assertNotNull(account);

            printCustomerAccount(account);

            IndividualCustomer customer1 = (IndividualCustomer) rootEntities.get(1);
            Assert.assertNotNull(customer1);

            printCustomerData("Customer1", customer1);
            printCommunicationInfo("Customer1", customer1.getCommunicationInfo());
            printMajorAccount("Customer1", customer1);
            printMergedFrom("Customer1", customer1);
            printMergedTo("Customer1", customer1);
            printSplitFrom("Customer1", customer1);
            printSplitTo("Customer1", customer1);
            printProductsOwned("Customer1", customer1);
            printCustomerDetails("Customer1", customer1);
            printGroupInfo("Customer1", customer1);

            IndividualCustomer customer2 = (IndividualCustomer) rootEntities.get(2);
            Assert.assertNotNull(customer2);

            printCustomerData("Customer2", customer2);
            printCommunicationInfo("Customer2", customer2.getCommunicationInfo());
            printGroupInfo("Customer2", customer2);

            OrganizationCustomer customer3 = (OrganizationCustomer) rootEntities.get(3);
            Assert.assertNotNull(customer3);

            printCustomerData("Customer3", customer3);
            printCommunicationInfo("Customer3", customer3.getCommunicationInfo());

            Opportunity opportunity1 = (Opportunity) rootEntities.get(4);
            Assert.assertNotNull(opportunity1);

            printOpportunityData("opportunity1", opportunity1);

            Opportunity opportunity2 = (Opportunity) rootEntities.get(5);
            Assert.assertNotNull(opportunity2);

            printOpportunityData("opportunity2", opportunity2);

            IndividualAgencyContainer individualAgencyContainer = (IndividualAgencyContainer) rootEntities.get(6);
            Assert.assertNotNull(individualAgencyContainer);

            printIndividualAgencyContainer(individualAgencyContainer);

            OrganizationAgencyContainer organizationAgencyContainer = (OrganizationAgencyContainer) rootEntities.get(7);
            Assert.assertNotNull(organizationAgencyContainer);

            printOrganizationAgencyContainer(organizationAgencyContainer);

            if (conversionResponse.getMessages() != null) {
                for (ConversionStatusMessage conversionStatusMessage : conversionResponse.getMessages()) {

                    ConversionActionStatus conversionActionStatus = (ConversionActionStatus) conversionStatusMessage;
                    System.out.println("ConversionResponse - Action: " + conversionActionStatus.getAction());
                    System.out.println("ConversionResponse - Status: " + conversionActionStatus.getStatus().getName());

                    for (ConversionStatusMessage statusMessage : conversionActionStatus.getMessages()) {
                        System.out.println("ConversionResponse - Error: " + statusMessage.getMessage());
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println("Response = " + conversionResponse.getMessage());
            e.printStackTrace();
        }
    }

    private void printCustomerAccount(GenesisCustomerAccount account) {

        Assert.assertEquals("A600001", account.getAccountNumber());
        System.out.println("Account - AccountNumber: " + account.getAccountNumber());

        Assert.assertEquals("CUSTOMERACCOUNT", account.getModelName());
        System.out.println("Account - ModelName: " + account.getModelName());

        Assert.assertEquals("ABC", account.getName());
        System.out.println("Account - Name: " + account.getName());

        Assert.assertEquals("uninitialized", account.getState());
        System.out.println("Account - State: " + account.getState());

        for (EntityLink<GroupInfo> groupInfo : account.getAccountGroupInfos()) {
            System.out.println("Account - GroupInfo: " + groupInfo.getURIString());
        }
    }

    private void printCustomerData(String customerId, Customer customer) {

        Assert.assertTrue(StringUtils.hasText(customer.getCustomerNumber()));
        System.out.println(customerId + " - CustomerNumber: " + customer.getCustomerNumber());

        Assert.assertTrue(StringUtils.hasText(customer.getState()));
        System.out.println(customerId + " - State: " + customer.getState());

        Assert.assertTrue(StringUtils.hasText(customer.getModelName()));
        System.out.println(customerId + " - ModelName: " + customer.getModelName());

        Assert.assertNotNull(customer.getAccessTrackInfo());
        if (customer.getAccessTrackInfo() != null) {
            System.out.println(
                    customerId + " - AccessTrackInfo - UpdatedBy: " + customer.getAccessTrackInfo().getUpdatedBy());
            System.out.println(
                    customerId + " - AccessTrackInfo - UpdatedOn: " + customer.getAccessTrackInfo().getUpdatedOn());
        }
    }

    private void printCommunicationInfo(String customerId, CrmCommunicationInfo commInfo) {

        if (commInfo != null) {
            System.out.println(customerId + " - CommunicationInfo - PreferredContactMethod: "
                    + commInfo.getPreferredContactMethod());

            if (commInfo.getAddresses() != null && !commInfo.getAddresses().isEmpty()) {

                for (CrmAddress address : commInfo.getAddresses()) {
                    System.out.println(customerId + " - CommunicationInfo - Adress - addressValidatedInd: "
                            + address.getAddressValidatedInd());

                    // Address Location
                    System.out.println(customerId + " - CommunicationInfo - Adress - Location - addressLine1: "
                            + address.getLocation().getAddressLine1());
                    System.out.println(customerId + " - CommunicationInfo - Adress - Location - city: "
                            + address.getLocation().getCity());
                    System.out.println(customerId + " - CommunicationInfo - Adress - Location - postalCode: "
                            + address.getLocation().getPostalCode());
                    System.out.println(customerId + " - CommunicationInfo - Adress - Location - stateProvinceCd: "
                            + address.getLocation().getStateProvinceCd());
                    System.out.println(customerId + " - CommunicationInfo - Adress - Location - countryCd: "
                            + address.getLocation().getCountryCd());
                }
            }
            if (!commInfo.getPhones().isEmpty()) {
                for (CrmPhone phone : commInfo.getPhones()) {
                    System.out.println(customerId + " - CommunicationInfo - Phone: " + phone.getValue());
                }
            }
        }
    }

    private void printMajorAccount(String customerId, IndividualCustomer customer) {

        Assert.assertEquals("12345", customer.getMajorAccount().getURIString());
        System.out.println(customerId + " - MajorAccount: " + customer.getMajorAccount().getURIString());

        Assert.assertEquals("12345", customer.getMajorAccountId());
        System.out.println(customerId + " - MajorAccountId: " + customer.getMajorAccountId());

        Assert.assertEquals("ABC", customer.getMajorAccountName());
        System.out.println(customerId + " - MajorAccountName: " + customer.getMajorAccountName());
    }

    private void printMergedFrom(String customerId, IndividualCustomer customer) {

        if (customer.getMergedFrom() != null) {
            for (EntityLink<com.eisgroup.genesis.factory.modeling.types.immutable.Customer> current : customer
                    .getMergedFrom()) {
                System.out.println(customerId + " - MergedFrom - customerId: " + current.getURIString());
            }
        }
    }

    private void printMergedTo(String customerId, IndividualCustomer customer) {

        if (customer.getMergedTo() != null) {
            EntityLink<com.eisgroup.genesis.factory.modeling.types.immutable.Customer> current = customer.getMergedTo();
            System.out.println(customerId + " - MergedTo - customerId: " + current.getURIString());
        }
    }

    private void printSplitFrom(String customerId, IndividualCustomer customer) {

        if (customer.getSplitFrom() != null) {
            EntityLink<com.eisgroup.genesis.factory.modeling.types.immutable.Customer> current = customer
                    .getSplitFrom();
            System.out.println(customerId + " - SplitFrom - customerId: " + current.getURIString());
        }
    }

    private void printSplitTo(String customerId, IndividualCustomer customer) {

        if (customer.getSplitTo() != null) {
            for (EntityLink<com.eisgroup.genesis.factory.modeling.types.immutable.Customer> current : customer
                    .getSplitTo()) {
                System.out.println(customerId + " - SplitTo - customerId: " + current.getURIString());
            }
        }
    }

    private void printProductsOwned(String customerId, IndividualCustomer customer) {

        if (customer.getProductsOwned() != null) {

            Collection<? extends ProductOwned> productsOwned = customer.getProductsOwned();

            for (ProductOwned genesisCrmProductOwned : productsOwned) {
                System.out
                        .println(customerId + " - GenesisCrmProductOwned: " + genesisCrmProductOwned.getPolicyNumber());
            }
        }
    }

    private void printCustomerDetails(String customerId, IndividualCustomer customer) {

        if (customer.getDetails() != null) {
            System.out.println(
                    customerId + " - Details - FirstName: " + customer.getDetails().getPerson().getFirstName());
            System.out
                    .println(customerId + " - Details - LastName: " + customer.getDetails().getPerson().getLastName());
            System.out.println(
                    customerId + " - Details - DateOfBirth: " + customer.getDetails().getPerson().getBirthDate());
        }
    }

    private void printGroupInfo(String customerId, Customer customer) {

        if (customer.getCustomerGroupInfos() != null) {
            for (com.eisgroup.genesis.factory.modeling.types.immutable.CustomerGroupInfo customerGroupsInfo : customer
                    .getCustomerGroupInfos()) {
                System.out.println(
                        customerId + " - GroupInfo - groupId: " + customerGroupsInfo.getGroupInfo().getURIString());
            }
        }
    }

    private void printOpportunityData(String opportunityId, Opportunity opportunity) {

        Assert.assertTrue(StringUtils.hasText(opportunity.getOpportunityId()));
        System.out.println(opportunityId + " - ID: " + opportunity.getOpportunityId());

        Assert.assertEquals("BROKER", opportunity.getChannel());
        System.out.println(opportunityId + " - Channel: " + opportunity.getChannel());

        Assert.assertEquals("LOW", opportunity.getLikelihood());
        System.out.println(opportunityId + " - Likelihood: " + opportunity.getLikelihood());

        if (((GenesisOpportunity) opportunity).getGenesisUserOwnerOwner().isPresent()) {
            GenesisUserOwner genesisUserOwner = ((GenesisOpportunity) opportunity).getGenesisUserOwnerOwner().get();

            System.out.println(
                    opportunityId + " - GenesisUserOwner - link: " + genesisUserOwner.getLink().getURIString());
        }
        if (((GenesisOpportunity) opportunity).getGenesisWorkQueueOwner().isPresent()) {
            GenesisWorkQueue genesisWorkQueue = ((GenesisOpportunity) opportunity).getGenesisWorkQueueOwner().get();

            System.out.println(opportunityId + " - GenesisWorkQueue - name: " + genesisWorkQueue.getName());
        }
        for (EntityAssociation entityAssociation : opportunity.getAssociations()) {
            System.out.println(opportunityId + " - Association: " + entityAssociation.getEntityNumber());
        }
    }

    private void printIndividualAgencyContainer(IndividualAgencyContainer individualAgencyContainer) {

        Assert.assertEquals("I", individualAgencyContainer.getAgency());
        System.out.println("IndividualAgencyContainer - Agency: " + individualAgencyContainer.getAgency());

        Assert.assertEquals("500000", individualAgencyContainer.getCustomer().getURIString());
        System.out.println(
                "IndividualAgencyContainer - Customer: " + individualAgencyContainer.getCustomer().getURIString());
    }

    private void printOrganizationAgencyContainer(OrganizationAgencyContainer organizationAgencyContainer) {

        Assert.assertEquals("O", organizationAgencyContainer.getAgency());
        System.out.println("OrganizationAgencyContainer - Agency: " + organizationAgencyContainer.getAgency());

        Assert.assertEquals("500003", organizationAgencyContainer.getCustomer().getURIString());
        System.out.println(
                "OrganizationAgencyContainer - Customer: " + organizationAgencyContainer.getCustomer().getURIString());
    }
}
