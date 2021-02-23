/*
 * Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.commands.crm;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import com.eisgroup.genesis.crm.repository.impl.ModelAwareNumberGenerator;
import com.eisgroup.genesis.crm.repository.impl.ModelAwareNumberGeneratorRegistry;
import com.eisgroup.genesis.crm.rules.validation.config.CrmKrakenValidationServiceConfig;
import com.eisgroup.genesis.decision.dimension.config.DecisionDimensionConfig;
import com.eisgroup.genesis.dimension.modeling.utils.EntityDimensionsUpdateValidator;
import com.eisgroup.genesis.kraken.repository.registry.config.KrakenRepositoryRegistryConfig;
import com.eisgroup.genesis.kraken.validation.config.KrakenValidationServiceConfig;
import com.eisgroup.genesis.rdf.repository.RelationshipRepository;
import com.eisgroup.genesis.uniquefield.finder.UniqueFieldsFinder;
import com.eisgroup.genesis.uniquefield.service.UniqueFieldService;
import com.eisgroup.genesis.uniquefield.validator.UniqueFieldsValidator;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.eisgroup.genesis.bam.events.ActivityEventPublisher;
import com.eisgroup.genesis.bam.events.ActivityStreamEventBuilderFactory;
import com.eisgroup.genesis.columnstore.ColumnStore;
import com.eisgroup.genesis.columnstore.statement.StatementBuilderFactory;
import com.eisgroup.genesis.commands.publisher.api.CommandPublisher;
import com.eisgroup.genesis.commands.services.CrmValidationService;
import com.eisgroup.genesis.commands.services.CustomerContactService;
import com.eisgroup.genesis.comparison.impl.config.ComparisonConfig;
import com.eisgroup.genesis.crm.repository.CrmWriteRepository;
import com.eisgroup.genesis.crm.repository.config.CustomerReadRepositoryConfig;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.modeling.types.CrmAddress;
import com.eisgroup.genesis.factory.modeling.types.CrmEmail;
import com.eisgroup.genesis.factory.modeling.types.IndividualCustomerBase;
import com.eisgroup.genesis.factory.services.AccessTrackInfoService;
import com.eisgroup.genesis.factory.services.ProductProvider;
import com.eisgroup.genesis.json.link.config.EntityLinkBuilderSpringConifg;
import com.eisgroup.genesis.json.link.config.EntityLinkResolverSpringConifg;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.search.schema.SearchSchemaRegistry;
import com.eisgroup.genesis.spring.config.SpringCommonsConfig;
import com.eisgroup.genesis.test.IntegrationTests;
import com.eisgroup.genesis.test.utils.JsonUtils;
import com.eisgroup.genesis.versioning.VersioningReadRepository;
import com.google.gson.JsonObject;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Andornchik
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTests.class)
@ContextConfiguration(classes = {KrakenRepositoryRegistryConfig.class,
        KrakenValidationServiceConfig.class,
        DecisionDimensionConfig.class,
        CrmKrakenValidationServiceConfig.class, ComparisonConfig.class,
        SpringCommonsConfig.class,
        EntityLinkResolverSpringConifg.class,
        EntityLinkBuilderSpringConifg.class,
        CustomerReadRepositoryConfig.class,
        IndCustomerWriteHandlerTest.TestConfig.class})
public class IndCustomerWriteHandlerTest {

    private static final String INDIVIDUAL_CUSTOMER_IMAGE = "individualCustomer.json";
    private static final String EMAIL_IMAGE = "email.json";
    private static final String MODEL_NAME = "INDIVIDUALCUSTOMER";

    @Autowired
    private WriteHandler writeHandler;

    @Test
    public void testGetEventNamesForNewEmail() {

        JsonObject previousEntity = createIndividualCustomer().toJson();
        IndividualCustomerBase individualCustomer = createIndividualCustomer();
        individualCustomer.getCommunicationInfo().setEmails(Arrays.asList(createEmail()));

        Collection<String> eventNames = writeHandler.getEventNames(individualCustomer, previousEntity);
        assertThat(eventNames.size(), equalTo(1));
        assertThat(eventNames.iterator().next(), equalTo("emails_create"));
    }

    @Test
    public void testGetEventNamesForCustomerUpdateAndAddressUpdate() {

        IndividualCustomerBase individualCustomer = createIndividualCustomer();
        JsonObject previousEntity = individualCustomer.toJson();

        individualCustomer = createIndividualCustomer();
        individualCustomer.getDetails().getPerson().setFirstName("updated_name");
        CrmAddress address = (CrmAddress) individualCustomer.getCommunicationInfo().getAddresses().iterator().next();
        address.getLocation().setAddressLine1("new address line 1");

        Collection<String> eventNames = writeHandler.getEventNames(individualCustomer, previousEntity);
        assertThat(eventNames.size(), equalTo(2));
        assertThat(eventNames, containsInAnyOrder("addresses_update", "update"));
    }

    @Test
    public void testGetEventNamesForRemovingFieldFromAddress() {

        IndividualCustomerBase individualCustomer = createIndividualCustomer();
        JsonObject previousEntity = individualCustomer.toJson();

        individualCustomer = createIndividualCustomer();
        CrmAddress address = (CrmAddress) individualCustomer.getCommunicationInfo().getAddresses().iterator().next();
        address.getLocation().setAddressLine1(null);

        Collection<String> eventNames = writeHandler.getEventNames(individualCustomer, previousEntity);
        assertThat(eventNames.size(), equalTo(1));
        assertThat(eventNames.iterator().next(), equalTo("addresses_update"));
    }

    private IndividualCustomerBase createIndividualCustomer() {
        JsonObject customerJson = JsonUtils.load(INDIVIDUAL_CUSTOMER_IMAGE);
        return (IndividualCustomerBase) ModelInstanceFactory.createInstance(MODEL_NAME, "1", customerJson);
    }

    private CrmEmail createEmail() {
        JsonObject emailJson = JsonUtils.load(EMAIL_IMAGE);
        return (CrmEmail) ModelInstanceFactory.createInstance(MODEL_NAME, "1", emailJson);
    }

    static class TestConfig {

        @Bean
        public WriteHandler writeHandler() {
            return new WriteHandler();
        }

        @Bean
        public ModelResolver modelResolver() {
            return new ModelResolver("INDIVIDUALCUSTOMER", "1");
        }

        @Bean
        public AccessTrackInfoService mockAccessTrackInfoService() {
            return mock(AccessTrackInfoService.class);
        }

        @Bean
        public CrmValidationService mockCrmValidationService() {
            return mock(CrmValidationService.class);
        }

        @Bean
        public ProductProvider mockProductProvider() {
            return mock(ProductProvider.class);
        }

        @Bean
        public ActivityEventPublisher mockActivityEventPublisher() {
            return mock(ActivityEventPublisher.class);
        }

        @Bean
        public ActivityStreamEventBuilderFactory mockActivityStreamEventBuilderFactory() {
            return mock(ActivityStreamEventBuilderFactory.class);
        }

        @Bean
        public VersioningReadRepository mockVersioningReadRepository() {
            return mock(VersioningReadRepository.class);
        }

        @Bean
        public ColumnStore mockColumnStore() {
            return mock(ColumnStore.class);
        }

        @Bean
        public StatementBuilderFactory mockStatementBuilderFactory() {
            return mock(StatementBuilderFactory.class);
        }

        @Bean(name="customerWriteRepository")
        public CrmWriteRepository mockCrmWriteRepository() {
            return mock(CrmWriteRepository.class);
        }

        @Bean
        public CommandPublisher mockCommandPublisher() {
            return mock(CommandPublisher.class);
        }

        @Bean
        public CustomerValidator customerValidator() {
            return new CustomerValidator();
        }

        @Bean
        public RelationshipRepository relationshipRepository() {
            return Mockito.mock(RelationshipRepository.class);
        }

        @Bean
        public ActivityHelper activityHelper() {
            return mock(ActivityHelper.class);
        }

        @Bean
        public UniqueFieldsFinder uniqueFieldsFinder() {
            return mock(UniqueFieldsFinder.class);
        }

        @Bean
        public UniqueFieldService uniqueFieldService() {
            return mock(UniqueFieldService.class);
        }

        @Bean
        public UniqueFieldsValidator uniqueFieldsValidator() {
            return mock(UniqueFieldsValidator.class);
        }

        @Bean
        public EntityDimensionsUpdateValidator entityDimensionsUpdateValidator() {
            return mock(EntityDimensionsUpdateValidator.class);
        }

        @Bean
        public SearchSchemaRegistry searchSchemaRegistry() {
            return new SearchSchemaRegistry();
        }

        @Bean
        public CustomerContactService customerContactService() {
            return new CustomerContactService();
        }

        @Bean
        public ModelAwareNumberGeneratorRegistry modelAwareNumberGeneratorRegistry() {
            return mock(ModelAwareNumberGeneratorRegistry.class);
        }

        @Bean
        public ModelAwareNumberGenerator modelAwareNumberGenerator() {
            ModelAwareNumberGenerator numberGenerator = Mockito.mock(ModelAwareNumberGenerator.class);
            when(numberGenerator.generate(Mockito.anyString())).thenReturn(RandomStringUtils.randomAlphanumeric(10));
            return numberGenerator;
        }
    }
}
