/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.conversion.services;

import com.eisgroup.genesis.factory.modeling.types.CustomerAssociation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/conversion-proto-crm-services-beans.xml"})
public class ConversionCustomerAccountFactoryServiceTest {

    @Resource(name = "conversionCustomerAccountFactoryService")
    private ConversionCustomerAccountFactoryService conversionCustomerAssociationFactory;

    @Test
    public void createAssociationFactory() {
        CustomerAssociation association = conversionCustomerAssociationFactory.createGenesisCustomerAssociation();
        Assert.assertNotNull(association);
    }
}
