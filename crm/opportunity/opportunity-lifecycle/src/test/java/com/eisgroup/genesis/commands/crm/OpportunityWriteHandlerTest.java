/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.commands.crm;

import com.eisgroup.genesis.bam.events.ActivityEventPublisher;
import com.eisgroup.genesis.bam.events.ActivityStreamEvent;
import com.eisgroup.genesis.bam.events.ActivityStreamEventBuilderFactory;
import com.eisgroup.genesis.columnstore.ColumnStore;
import com.eisgroup.genesis.columnstore.InMemoryColumnStoreConfig;
import com.eisgroup.genesis.columnstore.statement.StatementBuilderFactory;
import com.eisgroup.genesis.commands.crm.opportunity.WriteHandler;
import com.eisgroup.genesis.commands.opportunity.WriteHandlerRI;
import com.eisgroup.genesis.commands.publisher.api.CommandPublisher;
import com.eisgroup.genesis.commands.services.CrmValidationService;
import com.eisgroup.genesis.commands.services.DefaultOpportunityNotificationService;
import com.eisgroup.genesis.commands.services.OpportunityNotificationService;
import com.eisgroup.genesis.commands.services.opportunity.OpportunityValidationService;
import com.eisgroup.genesis.crm.commands.request.OpportunityWriteRequest;
import com.eisgroup.genesis.crm.repository.CrmReadRepository;
import com.eisgroup.genesis.crm.repository.CrmWriteRepository;
import com.eisgroup.genesis.crm.repository.config.CustomerRepositoryConfig;
import com.eisgroup.genesis.crm.repository.impl.DefaultCrmReadRepository;
import com.eisgroup.genesis.crm.repository.impl.DefaultCustomerWriteRepository;
import com.eisgroup.genesis.crm.rules.validation.CrmKrakenValidationService;
import com.eisgroup.genesis.decision.dimension.DimensionResolver;
import com.eisgroup.genesis.dimension.modeling.utils.EntityDimensionsUpdateValidator;
import com.eisgroup.genesis.entity.metadata.config.AnnotationEntityConfigurationProvider;
import com.eisgroup.genesis.entity.metadata.config.EntityConfigurationProvider;
import com.eisgroup.genesis.entity.metadata.mapper.ColumnValueMapperRegistry;
import com.eisgroup.genesis.entity.metadata.registry.EntityRegistry;
import com.eisgroup.genesis.facade.search.ModeledEntitySearchRepository;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.modeling.types.Opportunity;
import com.eisgroup.genesis.factory.services.AccessTrackInfoService;
import com.eisgroup.genesis.factory.services.ProductProvider;
import com.eisgroup.genesis.generator.EntityNumberGenerator;
import com.eisgroup.genesis.http.factory.ApacheHttpClientFactory;
import com.eisgroup.genesis.json.columnstore.config.ModeledEntityColumnStoreConfig;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.json.link.EntityLinkBuilderRegistry;
import com.eisgroup.genesis.json.link.config.EntityLinkBuilderSpringConifg;
import com.eisgroup.genesis.json.link.config.EntityLinkResolverSpringConifg;
import com.eisgroup.genesis.kraken.repository.registry.EntryPointRepositoryRegistry;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.rdf.repository.RelationshipRepository;
import com.eisgroup.genesis.search.config.SearchIndexSpringConfig;
import com.eisgroup.genesis.test.IntegrationTests;
import com.eisgroup.genesis.test.utils.JsonUtils;
import com.eisgroup.genesis.versioning.*;
import com.eisgroup.genesis.versioning.config.VersioningEntitySchemaConfiguration;
import com.google.gson.JsonObject;
import io.reactivex.Single;
import org.apache.commons.lang.RandomStringUtils;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Valeriy Sizonenko
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTests.class)
@ContextConfiguration(classes = {OpportunityWriteHandlerTest.TestConfig.class,
        InMemoryColumnStoreConfig.class,
        ModeledEntityColumnStoreConfig.class,
        EntityLinkResolverSpringConifg.class,
        EntityLinkBuilderSpringConifg.class,
        CustomerRepositoryConfig.class,
        SearchIndexSpringConfig.class
})
public class OpportunityWriteHandlerTest {

    private static final String OPPORTUNITY_JSON = "opportunityWriteHandler.json";
    private static final String MODEL_NAME = "Opportunity";

    @Autowired
    private WriteHandler writeHandler;

    @Autowired
    private CrmWriteRepository writeRepository;

    @Autowired
    private CrmReadRepository crmReadRepository;

    private Opportunity opportunity;

    @Before
    public void init() {
        opportunity = createOpportunity();
    }

    @Test
    public void writeOpportunityWithKey() {
        RootEntityKey.generateInitialKey(opportunity.getKey());

        when(writeRepository.save(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(opportunity.toJson());
        when(crmReadRepository.loadCurrent(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Single.just(new JsonObject()));

        Single<Opportunity> single = writeHandler.save(null, opportunity);
        Assert.assertEquals("Opportunity not saved", single.blockingGet(), opportunity);
    }

    @Test
    public void loadOpportunityWithNullKey() {
        Single<Opportunity> opportunitySingle = writeHandler.load(new OpportunityWriteRequest(opportunity));
        Assert.assertNull("OpportunityId was generated", opportunity.getOpportunityId());
        Assert.assertNotNull("Opportunity key wasn't generated", opportunity.getKey().getRootId());
        Assert.assertNull("Previous opportunity was returned", opportunitySingle.blockingGet().getOpportunityId());
    }

    private Opportunity createOpportunity() {
        JsonObject opportunityJson = JsonUtils.load(OPPORTUNITY_JSON);
        return (Opportunity) ModelInstanceFactory.createInstance(opportunityJson);
    }

    static class TestConfig {

        @Bean
        public WriteHandler writeHandler() {
            return new WriteHandlerRI();
        }

        @Bean
        public OpportunityNotificationService opportunityNotificationService() {
            return new DefaultOpportunityNotificationService(new ApacheHttpClientFactory());
        }

        @Bean
        public EntityNumberGenerator entityNumberGenerator() {
            EntityNumberGenerator entityNumberGenerator = mock(EntityNumberGenerator.class);
            when(entityNumberGenerator.generate(Mockito.anyString())).thenReturn(RandomStringUtils.randomAlphanumeric(10));
            return entityNumberGenerator;
        }

        @Bean
        public ModelResolver modelResolver() {
            return new ModelResolver(MODEL_NAME, "1");
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
        public CrmKrakenValidationService crmKrakenValidationService() {
            return mock(CrmKrakenValidationService.class);
        }

        @Bean
        public EntryPointRepositoryRegistry entryPointRepositoryRegistry() {
            return mock(EntryPointRepositoryRegistry.class);
        }

        @Bean
        public DimensionResolver dimensionResolver() {
            return mock(DimensionResolver.class);
        }

        @Bean
        public RelationshipRepository relationshipRepository() {
            return mock(RelationshipRepository.class);
        }

        @Bean
        public ProductProvider mockProductProvider() {
            return mock(ProductProvider.class);
        }


        @Bean
        public EntityRegistry entityRegistry() {
            return new EntityRegistry();
        }

        @Bean
        public ActivityEventPublisher activityEventPublisher() {
            return mock(ActivityEventPublisher.class);
        }

        @Bean
        public ActivityStreamEventBuilderFactory activityStreamEventBuilderFactory(EntityLinkBuilderRegistry entityLinkBuilderRegistry) {
            return mock(ActivityStreamEventBuilderFactory.class);
        }

        @Bean
        public StatementBuilderFactory statementBuilderFactory(EntityRegistry entityRegistry, ColumnValueMapperRegistry columnValueMapperRegistry) {
            return new StatementBuilderFactory(entityRegistry, columnValueMapperRegistry);
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
        public VersionSchemaRegistry versionSchemaRegistry() {
            return new VersionSchemaRegistry();
        }

        @Bean(name = "customerWriteRepository")
        public CrmWriteRepository crmWriteRepository() {
            return mock(DefaultCustomerWriteRepository.class);
        }

        @Bean
        public EntityConfigurationProvider entityConfigurationProvider() {
            return new AnnotationEntityConfigurationProvider();
        }

        @Bean(name = "customerReadRepository")
        public CrmReadRepository crmReadRepository() {
            return mock(DefaultCrmReadRepository.class);
        }

        @Bean
        public CommandPublisher mockCommandPublisher() {
            return mock(CommandPublisher.class);
        }

        @Bean
        public CustomerValidator customerValidator() {
            return mock(CustomerValidator.class);
        }

        @Bean
        public ActivityHelper activityHelper() {
            ActivityHelper activityHelper = Mockito.mock(ActivityHelper.class);
            Mockito.doNothing().when(activityHelper).publishActivityEvents(Mockito.any(), Mockito.any(), Mockito.any());
            when(activityHelper.activity(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                    .thenReturn(new ActivityStreamEvent(null, null, null, null, null, null, null, null, null, null, null));
            return activityHelper;
        }

        @Bean
        public EntityDimensionsUpdateValidator entityDimensionsUpdateValidator() {
            return mock(EntityDimensionsUpdateValidator.class);
        }

        @Bean
        public OpportunityValidationService opportunityValidationService() {
            return new OpportunityValidationService();
        }

        @Bean
        public ModeledEntitySearchRepository modeledEntitySearchRepository() {
            return mock(ModeledEntitySearchRepository.class);
        }
    }
}
