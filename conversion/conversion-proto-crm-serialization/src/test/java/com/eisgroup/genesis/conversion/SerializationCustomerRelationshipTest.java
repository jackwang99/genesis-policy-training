/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.conversion;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.eisgroup.genesis.conversion.domain.ConversionReaderModel;
import com.eisgroup.genesis.conversion.response.ConversionResponse;
import com.eisgroup.genesis.conversion.services.ConversionSerializationService;
import com.eisgroup.genesis.factory.model.individualcustomer.GenesisCustomerRelationship;
import com.eisgroup.genesis.factory.model.individualcustomer.IndividualCustomer;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.eisgroup.genesis.factory.modeling.types.IndividualAgencyContainer;
import com.eisgroup.genesis.factory.modeling.types.immutable.CustomerRelationship;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/conversion-serialization-beans.xml",
        "/META-INF/spring/conversion-crm-serialization-beans.xml", "/META-INF/spring/conversion-crm-bindings-beans.xml",
        "/META-INF/spring/conversion-common-beans.xml", "/META-INF/spring/conversion-bindings-beans.xml" })
public class SerializationCustomerRelationshipTest {

    @Resource(name = "conversionCustomerRelationshipSerializationService")
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
                    this.getClass().getClassLoader().getResource("customerRelationship.xml").getFile());

            String sourceFileBody = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8.name());
            System.out.println("Validating XML against the schema");

            ConversionReaderModel conversionReaderModel = (ConversionReaderModel) conversionSerializationService
                    .deserializeEntity(sourceFileBody, conversionResponse);

            List<RootEntity> rootEntities = conversionReaderModel.getRootEntities();

            IndividualCustomer customer = (IndividualCustomer) rootEntities.get(0);

            printCustomerData("Customer", customer);
            printCustomerRelationships("Customer", customer);

            IndividualAgencyContainer individualAgencyContainer = (IndividualAgencyContainer) rootEntities.get(1);
            printIndividualAgencyContainer("IndividualAgencyContainer", individualAgencyContainer);
            printCustomerRelationships("IndividualAgencyContainer", individualAgencyContainer);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printCustomerData(String customerId, Customer customer) {

        Assert.assertEquals("IC0000001031", customer.getCustomerNumber());
        System.out.println(customerId + " - CustomerNumber: " + customer.getCustomerNumber());

        Assert.assertEquals("INDIVIDUALCUSTOMER", customer.getModelName());
        System.out.println(customerId + " - ModelName: " + customer.getModelName());
    }

    private void printIndividualAgencyContainer(String containerId,
            IndividualAgencyContainer individualAgencyContainer) {

        Assert.assertEquals("A", individualAgencyContainer.getAgency());
        System.out.println(containerId + " - Agency: " + individualAgencyContainer.getAgency());

        Assert.assertEquals("IC500001", individualAgencyContainer.getCustomer().getURIString());
        System.out.println(containerId + " - Customer: " + individualAgencyContainer.getCustomer().getURIString());

        Assert.assertEquals("IndividualAgencyContainer", individualAgencyContainer.getModelName());
        System.out.println(containerId + " - ModelName: " + individualAgencyContainer.getModelName());

        Assert.assertEquals("active", individualAgencyContainer.getState());
        System.out.println(containerId + " - State: " + individualAgencyContainer.getState());
    }

    private void printCustomerRelationships(String customerId, Customer customer) {

        if (customer.getRelationships() != null) {
            for (CustomerRelationship customerRelationship : customer.getRelationships()) {

                GenesisCustomerRelationship genesisCustomerRelationship = (GenesisCustomerRelationship) customerRelationship;

                Assert.assertEquals("answer", genesisCustomerRelationship.getAnswer());
                System.out.println(
                        customerId + " - GenesisCustomerRelationship: " + genesisCustomerRelationship.getAnswer());
            }
        }
    }

    private void printCustomerRelationships(String containerId, IndividualAgencyContainer individualAgencyContainer) {

        if (individualAgencyContainer.getRelationships() != null) {
            for (CustomerRelationship customerRelationship : individualAgencyContainer.getRelationships()) {

                com.eisgroup.genesis.factory.model.individualagencycontainer.GenesisCustomerRelationship genesisCustomerRelationship = (com.eisgroup.genesis.factory.model.individualagencycontainer.GenesisCustomerRelationship) customerRelationship;
                Assert.assertEquals("answer", genesisCustomerRelationship.getAnswer());
                System.out.println(
                        containerId + " - GenesisCustomerRelationship: " + genesisCustomerRelationship.getAnswer());
            }
        }
    }

}
