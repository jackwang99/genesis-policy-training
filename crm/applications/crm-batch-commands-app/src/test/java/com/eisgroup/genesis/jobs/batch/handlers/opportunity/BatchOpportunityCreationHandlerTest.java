/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.jobs.batch.handlers.opportunity;

import static org.mockito.Mockito.mock;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.eisgroup.genesis.columnstore.ColumnStore;
import com.eisgroup.genesis.columnstore.statement.StatementBuilderFactory;
import com.eisgroup.genesis.crm.commands.CrmCommands;
import com.eisgroup.genesis.facade.search.ModeledEntitySearchRepository;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.json.IdentifiableEntity;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.eisgroup.genesis.factory.modeling.types.Opportunity;
import com.eisgroup.genesis.factory.modeling.types.immutable.OpportunityAssociation;
import com.eisgroup.genesis.factory.repository.links.config.FactoryLinkBuilderConfig;
import com.eisgroup.genesis.json.link.config.EntityLinkBuilderSpringConifg;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.spring.config.SpringCommonsConfig;
import com.eisgroup.genesis.factory.modeling.types.ProductOwned;
import com.eisgroup.genesis.test.IntegrationTests;
import com.eisgroup.genesis.test.utils.JsonUtils;
import com.google.gson.JsonObject;

/**
 * 
 * @author Dmitry Andronchik
 * @since 10.3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTests.class)
@ContextConfiguration(classes = { SpringCommonsConfig.class, 
                                  EntityLinkBuilderSpringConifg.class,
                                  FactoryLinkBuilderConfig.class,
                                  BatchOpportunityCreationHandlerTest.TestConfig.class })
@TestPropertySource(properties = { "genesis.job.crm.opportunityOpenDays=60" }) 
public class BatchOpportunityCreationHandlerTest {

    private static final String INDIVIDUAL_CUSTOMER_IMAGE = "individualCustomer.json";
    private static final String PRODUCT_OWNED_IMAGE = "productOwned.json";
    private static final String MODEL_NAME = "INDIVIDUALCUSTOMER";
    private static final String MODEL_VERSION = "1";

    @Autowired
    private BatchOpportunityCreationHandler handler;

    @Test
    public void testGetCommandNameToBeExecuted() {
        Assert.assertEquals(CrmCommands.WRITE_OPPORTUNITY, handler.getCommandNameToBeExecuted());
    }

    @Test
    public void testCreateOpportunity() {

        Customer customer = (Customer) loadEntity(INDIVIDUAL_CUSTOMER_IMAGE);
        ProductOwned productOwned = (ProductOwned) loadEntity(PRODUCT_OWNED_IMAGE);

        Opportunity opportunity = handler.createOpportunity(customer, productOwned);

        Assert.assertEquals("DIRECT", opportunity.getChannel());
        Assert.assertEquals("UNKNOWN", opportunity.getLikelihood());
        Assert.assertEquals("draft", opportunity.getState());

        Assert.assertEquals(
                "Policy Conversion Opportunity: AU Policy# P0000006 with carrier1 carrierDesc is approaching its Expiration Date of 2028-02-02",
                opportunity.getDescription());

        Assert.assertEquals(1, opportunity.getAssociations().size());
        OpportunityAssociation opportunityAssociation = opportunity.getAssociations().iterator().next();
        Assert.assertEquals(customer.getCustomerNumber(), opportunityAssociation.getEntityNumber());

        Assert.assertEquals("geroot://Customer/INDIVIDUALCUSTOMER//910fba51-b01d-4c20-a457-99c70fb738ab",
                opportunityAssociation.getLink().getURIString());
    }

    private IdentifiableEntity loadEntity(String image) {
        JsonObject entityJson = JsonUtils.load(image);
        return (IdentifiableEntity) ModelInstanceFactory.createInstance(MODEL_NAME, MODEL_VERSION, entityJson);
    }

    static class TestConfig {

        @Bean
        public BatchOpportunityCreationHandler batchOpportunityCreationHandler() {
            return new BatchOpportunityCreationHandler();
        }

        @Bean
        public ModelResolver modelResolver() {
            return new ModelResolver(MODEL_NAME, MODEL_VERSION);
        }

        @Bean
        public ColumnStore mockColumnStore() {
            return mock(ColumnStore.class);
        }

        @Bean
        public StatementBuilderFactory mockStatementBuilderFactory() {
            return mock(StatementBuilderFactory.class);
        }

        @Bean
        public ModeledEntitySearchRepository mockModeledEntitySearchRepository() {
            return mock(ModeledEntitySearchRepository.class);
        }
    }
}
