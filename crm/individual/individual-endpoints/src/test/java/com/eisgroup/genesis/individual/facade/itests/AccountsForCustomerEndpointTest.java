/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.individual.facade.itests;

import com.eisgroup.genesis.bam.events.ActivityEventPublisher;
import com.eisgroup.genesis.bam.events.ActivityStreamEventBuilderFactory;
import com.eisgroup.genesis.columnstore.ColumnStore;
import com.eisgroup.genesis.columnstore.InMemoryColumnStoreConfig;
import com.eisgroup.genesis.columnstore.statement.StatementBuilderFactory;
import com.eisgroup.genesis.commands.crm.ActivityHelper;
import com.eisgroup.genesis.commands.crm.communication.WriteHandler;
import com.eisgroup.genesis.commands.services.CrmValidationService;
import com.eisgroup.genesis.crm.core.service.ModelRepositoryService;
import com.eisgroup.genesis.crm.core.service.RelationshipService;
import com.eisgroup.genesis.crm.core.service.customer.CustomerAccountsService;
import com.eisgroup.genesis.crm.repository.CrmReadRepository;
import com.eisgroup.genesis.crm.repository.CrmWriteRepository;
import com.eisgroup.genesis.crm.repository.config.CustomerReadRepositoryConfig;
import com.eisgroup.genesis.crm.repository.config.CustomerRepositoryConfig;
import com.eisgroup.genesis.crm.repository.impl.DefaultCrmReadRepository;
import com.eisgroup.genesis.crm.repository.impl.DefaultCustomerWriteRepository;
import com.eisgroup.genesis.crm.rules.validation.config.CrmKrakenValidationServiceConfig;
import com.eisgroup.genesis.decision.dimension.config.DecisionDimensionConfig;
import com.eisgroup.genesis.dimension.modeling.utils.EntityDimensionsUpdateValidator;
import com.eisgroup.genesis.facade.AccountsForCustomerEndpoint;
import com.eisgroup.genesis.facade.endpoint.load.LoadEntityRootRequest;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.eisgroup.genesis.factory.modeling.types.CustomerAccount;
import com.eisgroup.genesis.factory.repository.links.FactoryLinkBuilder;
import com.eisgroup.genesis.factory.repository.links.FactoryLinkResolver;
import com.eisgroup.genesis.factory.services.AccessTrackInfoService;
import com.eisgroup.genesis.factory.services.ProductProvider;
import com.eisgroup.genesis.generator.EntityNumberGenerator;
import com.eisgroup.genesis.generator.SequenceGenerator;
import com.eisgroup.genesis.generator.impl.NoopSequenceGenerator;
import com.eisgroup.genesis.generator.impl.SimpleNumberGenerator;
import com.eisgroup.genesis.json.columnstore.config.ModeledEntityColumnStoreConfig;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.json.link.EntityLink;
import com.eisgroup.genesis.json.link.EntityLinkBuilderRegistry;
import com.eisgroup.genesis.json.link.EntityLinkResolverRegistry;
import com.eisgroup.genesis.json.link.config.EntityLinkResolverSpringConifg;
import com.eisgroup.genesis.kraken.repository.registry.config.KrakenRepositoryRegistryConfig;
import com.eisgroup.genesis.kraken.validation.config.KrakenValidationServiceConfig;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.rdf.registry.PredicateRegistry;
import com.eisgroup.genesis.rdf.repository.RelationshipRepository;
import com.eisgroup.genesis.rdf.repository.config.RdfEntityConfig;
import com.eisgroup.genesis.rdf.repository.impl.RelationshipRepositoryImpl;
import com.eisgroup.genesis.repository.TargetEntityNotFoundException;
import com.eisgroup.genesis.search.config.SearchIndexSpringConfig;
import com.eisgroup.genesis.test.IntegrationTests;
import com.eisgroup.genesis.test.utils.JsonUtils;
import com.eisgroup.genesis.versioning.VersioningConfig;
import com.eisgroup.genesis.versioning.config.VersioningRepoConfig;
import com.google.gson.JsonObject;
import io.reactivex.Observable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * {@link AccountsForCustomerEndpointTest} integration test.
 *
 * @author Valeriy Sizonenko
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        AccountsForCustomerEndpointTest.Config.class,
        VersioningRepoConfig.class,
        VersioningConfig.class,
        InMemoryColumnStoreConfig.class,
        ModeledEntityColumnStoreConfig.class,
        EntityLinkResolverSpringConifg.class,
        CustomerReadRepositoryConfig.class,
        CustomerRepositoryConfig.class,
        SearchIndexSpringConfig.class,
        KrakenRepositoryRegistryConfig.class,
        KrakenValidationServiceConfig.class,
        CrmKrakenValidationServiceConfig.class,
        DecisionDimensionConfig.class
})
@Category(IntegrationTests.class)
public class AccountsForCustomerEndpointTest {

    private static final String ACCOUNT_JSON = "accountWriteHandler.json";
    private static final String CUSTOMER_JSON = "individualCustomerWriteHandler.json";
    private static final String IND_MODEL_NAME = "INDIVIDUALCUSTOMER";
    private static final String ORG_MODEL_NAME = "ORGANIZATIONCUSTOMER";

    @Autowired
    private AccountsForCustomerEndpoint accountsForCustomerEndpoint;
    @Autowired
    private RelationshipService relationshipService;

    private LoadEntityRootRequest request;

    @Before
    public void setUp() {
        request = mock(LoadEntityRootRequest.class);
        when(request.getKey()).thenReturn(new RootEntityKey(UUID.randomUUID(), 1));
        when(request.getOffset()).thenReturn(0);
        when(request.getLimit()).thenReturn(1);
        when(request.getModelName()).thenReturn(IND_MODEL_NAME);
    }

    @Test(expected = TargetEntityNotFoundException.class)
    public void loadRelationshipsTestFalse() {
        doThrow(new TargetEntityNotFoundException()).when(relationshipService).loadCurrent(Mockito.any(), Mockito.anyString());

        Observable<JsonObject> result = accountsForCustomerEndpoint.loadRelationships(request);
        Assert.assertNull("Account was found", result.blockingFirst());
    }

    @Test
    public void loadRelationshipsTest() {
        CustomerAccount customerAccount = (CustomerAccount) ModelInstanceFactory.createInstance(JsonUtils.load(ACCOUNT_JSON));
        Customer customer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(CUSTOMER_JSON));

        doReturn(Observable.just(customer.toJson())).when(relationshipService).loadCurrent(Mockito.any(), Mockito.anyString());

        EntityLink customerRootLink = mock(EntityLink.class);
        doReturn(customerRootLink).when(relationshipService).createLink(customer.toJson(),true, Customer.class);
        when(customerRootLink.getURIString()).thenReturn("gentity://Customer/INDIVIDUALCUSTOMER//");

        EntityLink customerLink = mock(EntityLink.class);
        doReturn(customerLink).when(relationshipService).createLink(customer.toJson(),false, Customer.class);
        when(customerLink.getURIString()).thenReturn("gentity://Customer/INDIVIDUALCUSTOMER//");

        doReturn(Observable.just(customerAccount.toJson())).when(relationshipService).resolveLinks(Mockito.anyList(), Mockito.anyCollection());

        when(request.getKey()).thenReturn(customer.getKey());
        Observable<JsonObject> result = accountsForCustomerEndpoint.loadRelationships(request);
        Assert.assertNotNull("Account not found", result.blockingFirst());
    }

    static class Config {

        @Bean
        public RelationshipService relationshipService() {
            return Mockito.spy(RelationshipService.class);
        }

        @Bean
        public ModelRepositoryService modelRepositoryService() {
            ModelRepositoryService modelRepositoryService = mock(ModelRepositoryService.class);
            when(modelRepositoryService.getModelNamesByModelType(Mockito.anyString())).thenReturn(Arrays.asList(IND_MODEL_NAME, ORG_MODEL_NAME));
            return modelRepositoryService;
        }

        @Bean
        SequenceGenerator sequenceGenerator(){
            return new NoopSequenceGenerator();
        }

        @Bean
        public EntityNumberGenerator communicationIdGenerator(SequenceGenerator sequenceGenerator){
            return new SimpleNumberGenerator("communication_id", "C%010d", sequenceGenerator);
        }

        @Bean
        public WriteHandler writeHandler() {
            return new WriteHandler();
        }

        @Bean
        public AccountsForCustomerEndpoint accountsForCustomerEndpoint() {
            return new AccountsForCustomerEndpoint();
        }

        @Bean(name = "customerReadRepository")
        public CrmReadRepository crmReadRepository() {
            return new DefaultCrmReadRepository();
        }

        @Bean(name = "communicationWriteRepository")
        public CrmWriteRepository crmWriteRepository() {
            return new DefaultCustomerWriteRepository();
        }

        @Bean
        public PredicateRegistry predicateRegistry() {
            return mock(PredicateRegistry.class);
        }

        @Bean
        public EntityLinkResolverRegistry entityLinkResolverRegistry() {
            return new EntityLinkResolverRegistry();
        }

        @Bean
        public RdfEntityConfig.RdfSchemaConfig tripletTableSchemaConfiguration() {
            return new RdfEntityConfig.RdfSchemaConfig();
        }

        @Bean
        public RelationshipRepository relationshipReadRepository(ColumnStore columnStore, StatementBuilderFactory statementBuilder, EntityLinkBuilderRegistry linkBuilderRegistry, RdfEntityConfig.RdfSchemaConfig schemaConfiguration) {
            return new RelationshipRepositoryImpl(columnStore, statementBuilder, schemaConfiguration);
        }

        @Bean
        public ModelResolver modelResolver() {
            return new ModelResolver(IND_MODEL_NAME, "1");
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
        public ActivityEventPublisher activityEventPublisher() {
            return mock(ActivityEventPublisher.class);
        }

        @Bean
        public ActivityHelper activityHelper() {
            return new ActivityHelper();
        }

        @Bean
        public ActivityStreamEventBuilderFactory activityStreamEventBuilderFactory() {
            return mock(ActivityStreamEventBuilderFactory.class);
        }

        @Bean
        public FactoryLinkBuilder factoryLinkBuilder() {
            return new FactoryLinkBuilder();
        }

        @Bean
        public FactoryLinkResolver factoryLinkResolver() {
            FactoryLinkResolver factoryLinkResolver = Mockito.spy(FactoryLinkResolver.class);
            when(factoryLinkResolver.getLinkScheme()).thenReturn("gentity");
            return factoryLinkResolver;
        }

        @Bean
        public EntityLinkBuilderRegistry entityLinkBuilderRegistry() {
            EntityLinkBuilderRegistry entityLinkBuilderRegistry = new EntityLinkBuilderRegistry();
            entityLinkBuilderRegistry.registerBuilder(factoryLinkBuilder());
            return entityLinkBuilderRegistry;
        }

        @Bean
        public EntityDimensionsUpdateValidator entityDimensionsUpdateValidator() {
            return mock(EntityDimensionsUpdateValidator.class);
        }
        
        @Bean
        public CustomerAccountsService customerAccountsService() {
            return new CustomerAccountsService();
        }
    }
}
