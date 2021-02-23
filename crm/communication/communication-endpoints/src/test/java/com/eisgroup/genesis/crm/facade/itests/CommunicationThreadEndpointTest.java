/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.crm.facade.itests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.UUID;

import com.eisgroup.genesis.crm.rules.validation.config.CrmKrakenValidationServiceConfig;
import com.eisgroup.genesis.decision.dimension.config.DecisionDimensionConfig;
import com.eisgroup.genesis.kraken.repository.registry.config.KrakenRepositoryRegistryConfig;
import com.eisgroup.genesis.kraken.validation.config.KrakenValidationServiceConfig;
import com.eisgroup.genesis.versioning.config.VersioningEntitySchemaConfiguration;
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

import com.eisgroup.genesis.bam.events.ActivityEventPublisher;
import com.eisgroup.genesis.bam.events.ActivityStreamEventBuilderFactory;
import com.eisgroup.genesis.columnstore.ColumnStore;
import com.eisgroup.genesis.columnstore.InMemoryColumnStoreConfig;
import com.eisgroup.genesis.columnstore.config.ColumnStoreSpringConfig;
import com.eisgroup.genesis.columnstore.statement.StatementBuilderFactory;
import com.eisgroup.genesis.commands.crm.ActivityHelper;
import com.eisgroup.genesis.commands.crm.communication.WriteHandler;
import com.eisgroup.genesis.commands.services.CrmValidationService;
import com.eisgroup.genesis.crm.core.service.RelationshipService;
import com.eisgroup.genesis.crm.repository.CrmReadRepository;
import com.eisgroup.genesis.crm.repository.CrmWriteRepository;
import com.eisgroup.genesis.crm.repository.config.CustomerRepositoryConfig;
import com.eisgroup.genesis.crm.repository.impl.DefaultCrmReadRepository;
import com.eisgroup.genesis.crm.repository.impl.DefaultCustomerWriteRepository;
import com.eisgroup.genesis.dimension.modeling.utils.EntityDimensionsUpdateValidator;
import com.eisgroup.genesis.facade.CommunicationThreadEndpoint;
import com.eisgroup.genesis.facade.endpoint.load.LoadEntityRootRequest;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.modeling.types.Communication;
import com.eisgroup.genesis.factory.repository.links.FactoryLinkResolver;
import com.eisgroup.genesis.factory.services.AccessTrackInfoService;
import com.eisgroup.genesis.factory.services.ProductProvider;
import com.eisgroup.genesis.json.columnstore.config.ModeledEntityColumnStoreConfig;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.json.link.EntityLinkBuilderRegistry;
import com.eisgroup.genesis.json.link.EntityLinkResolverRegistry;
import com.eisgroup.genesis.json.link.config.EntityLinkResolverSpringConifg;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.rdf.registry.PredicateRegistry;
import com.eisgroup.genesis.rdf.repository.RelationshipRepository;
import com.eisgroup.genesis.repository.TargetEntityNotFoundException;
import com.eisgroup.genesis.search.config.SearchIndexSpringConfig;
import com.eisgroup.genesis.test.IntegrationTests;
import com.eisgroup.genesis.test.utils.JsonUtils;
import com.eisgroup.genesis.versioning.DefaultVersioningReadRepository;
import com.eisgroup.genesis.versioning.DefaultVersioningStrategy;
import com.eisgroup.genesis.versioning.VersionSchemaRegistry;
import com.eisgroup.genesis.versioning.VersioningReadRepository;
import com.eisgroup.genesis.versioning.VersioningStrategyResolver;
import com.google.gson.JsonObject;
import com.eisgroup.genesis.generator.EntityNumberGenerator;
import com.eisgroup.genesis.generator.SequenceGenerator;
import com.eisgroup.genesis.generator.impl.NoopSequenceGenerator;
import com.eisgroup.genesis.generator.impl.SimpleNumberGenerator;

import io.reactivex.Observable;

/**
 * {@link CommunicationThreadEndpointTest} integration test.
 *
 * @author Valeriy Sizonenko
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        CommunicationThreadEndpointTest.Config.class,
        InMemoryColumnStoreConfig.class,
        ModeledEntityColumnStoreConfig.class,
        ColumnStoreSpringConfig.class,
        EntityLinkResolverSpringConifg.class,
        CustomerRepositoryConfig.class,
        SearchIndexSpringConfig.class,
        KrakenRepositoryRegistryConfig.class,
        KrakenValidationServiceConfig.class,
        CrmKrakenValidationServiceConfig.class,
        DecisionDimensionConfig.class
})
@Category(IntegrationTests.class)
public class CommunicationThreadEndpointTest {

    private static final String COMMUNICATION_CHILD_JSON = "communicationChildWriteHandler.json";
    private static final String COMMUNICATION_PARENT_JSON = "communicationParentWriteHandler.json";
    private static final String MODEL_NAME = "Communication";

    @Autowired
    private CommunicationThreadEndpoint threadEndpoint;
    @Autowired
    private CrmWriteRepository crmWriteRepository;

    private LoadEntityRootRequest request;

    @Before
    public void setUp() {
        request = mock(LoadEntityRootRequest.class);
        when(request.getLimit()).thenReturn(10);
        when(request.getOffset()).thenReturn(0);
        when(request.getModelName()).thenReturn(MODEL_NAME);
        when(request.getFields()).thenReturn(Collections.singletonList("thread"));
    }

    @Test
    public void findCommunicationTest() {
        Communication childCommunication = createCommunication(COMMUNICATION_CHILD_JSON);
        crmWriteRepository.save(MODEL_NAME, childCommunication.toJson());

        Communication parentCommunication = createCommunication(COMMUNICATION_PARENT_JSON);
        crmWriteRepository.save(MODEL_NAME, parentCommunication.toJson());

        when(request.getKey()).thenReturn(new RootEntityKey(parentCommunication.getKey().getRootId(), parentCommunication.getKey().getRevisionNo()));
        Observable<JsonObject> result = threadEndpoint.loadThread(request);
        Assert.assertNotNull("Communication not found", result.blockingFirst());
    }

    @Test(expected = TargetEntityNotFoundException.class)
    public void findCommunicationTestFalse() {
        when(request.getKey()).thenReturn(new RootEntityKey(UUID.randomUUID(), 1));
        Observable<JsonObject> result = threadEndpoint.loadThread(request);
        Assert.assertNull("Communication not found", result.blockingFirst());
    }

    private Communication createCommunication(String json) {
        JsonObject opportunityJson = JsonUtils.load(json);
        return (Communication) ModelInstanceFactory.createInstance(opportunityJson);
    }

    static class Config {

        @Bean
        public WriteHandler writeHandler() {
            return new WriteHandler();
        }

        @Bean
        public VersioningStrategyResolver versioningStrategyResolver() {
            return mock(VersioningStrategyResolver.class);
        }

        @Bean
        public VersioningReadRepository versioningReadRepository(ColumnStore columnStore, StatementBuilderFactory statementBuilderFactory, 
                VersioningStrategyResolver versioningStrategyResolver, VersionSchemaRegistry versionSchemaRegistry) {
            when(versioningStrategyResolver.resolveByEntityType(Mockito.any(), Mockito.any())).thenReturn(new DefaultVersioningStrategy());
            return new DefaultVersioningReadRepository(columnStore, statementBuilderFactory, versioningStrategyResolver,
                    versionSchemaRegistry, new VersioningEntitySchemaConfiguration(), 14L);
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
        public VersionSchemaRegistry versionSchemaRegistry() {
            return new VersionSchemaRegistry();
        }

        @Bean
        public CommunicationThreadEndpoint threadEndpoint() {
            return new CommunicationThreadEndpoint();
        }

        @Bean
        public RelationshipRepository relationshipReadRepository() {
            return mock(RelationshipRepository.class);
        }

        @Bean
        public ModelResolver modelResolver() {
            return new ModelResolver(MODEL_NAME, "1");
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
        public EntityLinkBuilderRegistry entityLinkBuilderRegistry() {
            return new EntityLinkBuilderRegistry();
        }

        @Bean
        public EntityLinkResolverRegistry entityLinkResolverRegistry() {
            return new EntityLinkResolverRegistry();
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
        public FactoryLinkResolver factoryLinkResolver() {
            return new FactoryLinkResolver();
        }

        @Bean
        public RelationshipService relationshipService() {
            return new RelationshipService();
        }

        @Bean
        public EntityDimensionsUpdateValidator entityDimensionsUpdateValidator() {
            return mock(EntityDimensionsUpdateValidator.class);
        }
    }
}
