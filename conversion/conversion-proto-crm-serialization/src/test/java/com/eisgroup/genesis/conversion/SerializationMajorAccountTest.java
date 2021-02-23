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
import com.eisgroup.genesis.conversion.response.ConversionActionStatus;
import com.eisgroup.genesis.conversion.response.ConversionResponse;
import com.eisgroup.genesis.conversion.response.ConversionStatusMessage;
import com.eisgroup.genesis.conversion.services.ConversionSerializationService;
import com.eisgroup.genesis.factory.model.majoraccount.GenesisMajorAccount;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/conversion-serialization-beans.xml",
        "/META-INF/spring/conversion-crm-serialization-beans.xml", "/META-INF/spring/conversion-crm-bindings-beans.xml",
        "/META-INF/spring/conversion-common-beans.xml", "/META-INF/spring/conversion-bindings-beans.xml" })
public class SerializationMajorAccountTest {

    @Resource(name = "conversionMajorAccountSerializationService")
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
                    this.getClass().getClassLoader().getResource("majorAccount.xml").getFile());

            String sourceFileBody = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8.name());
            System.out.println("Validating XML against the schema");

            ConversionReaderModel conversionReaderModel = (ConversionReaderModel) conversionSerializationService
                    .deserializeEntity(sourceFileBody, conversionResponse);

            List<RootEntity> rootEntities = conversionReaderModel.getRootEntities();

            GenesisMajorAccount majorAccount = (GenesisMajorAccount) rootEntities.get(0);
            Assert.assertNotNull(majorAccount);

            Assert.assertEquals("12345", majorAccount.getAccountId());
            System.out.println("MajorAccount - AccountId: " + majorAccount.getAccountId());

            Assert.assertEquals("MajorAccount", majorAccount.getModelName());
            System.out.println("MajorAccount - ModelName: " + majorAccount.getModelName());

            Assert.assertEquals("ABC", majorAccount.getName());
            System.out.println("MajorAccount - Name: " + majorAccount.getName());

            Assert.assertEquals("active", majorAccount.getState());
            System.out.println("MajorAccount - State: " + majorAccount.getState());

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
            e.printStackTrace();
        }
    }
}
