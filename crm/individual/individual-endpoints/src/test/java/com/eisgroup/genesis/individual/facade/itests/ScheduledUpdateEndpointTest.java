/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.individual.facade.itests;

import java.time.LocalDate;

import com.eisgroup.genesis.columnstore.InMemoryColumnStoreConfig;
import com.eisgroup.genesis.comparison.impl.config.ComparisonConfig;
import com.eisgroup.genesis.crm.repository.impl.ScheduledUpdateRepository;
import com.eisgroup.genesis.crm.rules.validation.config.CrmKrakenValidationServiceConfig;
import com.eisgroup.genesis.decision.dimension.config.DecisionDimensionConfig;
import com.eisgroup.genesis.facade.error.ScheduledUpdateNotExistException;
import com.eisgroup.genesis.crm.entity.ScheduledUpdate;
import com.eisgroup.genesis.crm.repository.config.CustomerReadRepositoryConfig;
import com.eisgroup.genesis.crm.repository.config.CustomerRepositoryConfig;
import com.eisgroup.genesis.crm.repository.config.CustomerWriteRepositoryConfig;
import com.eisgroup.genesis.facade.ScheduledUpdateEndpoint;
import com.eisgroup.genesis.facade.request.GetScheduledUpdateRequest;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.eisgroup.genesis.factory.repository.ModeledEntitySchemaResolver;
import com.eisgroup.genesis.individual.facade.itests.config.ScheduledUpdateTestConfig;
import com.eisgroup.genesis.json.columnstore.config.ModeledEntityColumnStoreConfig;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.json.link.config.EntityLinkBuilderSpringConifg;
import com.eisgroup.genesis.json.link.config.EntityLinkResolverSpringConifg;
import com.eisgroup.genesis.kraken.repository.registry.config.KrakenRepositoryRegistryConfig;
import com.eisgroup.genesis.kraken.validation.config.KrakenValidationServiceConfig;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import com.eisgroup.genesis.search.config.SearchIndexSpringConfig;
import com.eisgroup.genesis.test.IntegrationTests;
import com.eisgroup.genesis.test.utils.JsonUtils;
import io.reactivex.Single;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link ScheduledUpdateEndpointTest} integration test.
 *
 * @author Valeriy Sizonenko
 */
@ContextConfiguration(classes = {
        InMemoryColumnStoreConfig.class,
        ModeledEntityColumnStoreConfig.class,
        ScheduledUpdateTestConfig.class,
        EntityLinkResolverSpringConifg.class,
        EntityLinkBuilderSpringConifg.class,
        CustomerReadRepositoryConfig.class,
        CustomerWriteRepositoryConfig.class,
        CustomerRepositoryConfig.class,
        ComparisonConfig.class,
        SearchIndexSpringConfig.class,
        KrakenRepositoryRegistryConfig.class,
        KrakenValidationServiceConfig.class,
        CrmKrakenValidationServiceConfig.class,
        DecisionDimensionConfig.class
})
@Category(IntegrationTests.class)
public class ScheduledUpdateEndpointTest extends AbstractJUnit4SpringContextTests {

    private static final String CUSTOMER_JSON = "individualCustomerWriteHandler.json";

    @Autowired
    private ScheduledUpdateRepository scheduledUpdateRepository;
    @Autowired
    private ScheduledUpdateEndpoint scheduledUpdateEndpoint;
    @Autowired
    private ModelResolver modelResolver;

    private GetScheduledUpdateRequest request;

    @Before
    public void setUp() {
        request = mock(GetScheduledUpdateRequest.class);
    }

    @Test(expected = ScheduledUpdateNotExistException.class)
    public void loadUpdateTestFalse() {
        Customer customer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(CUSTOMER_JSON));
        RootEntityKey.generateInitialKey(customer.getKey());
        when(request.getCustomerKey()).thenReturn(customer.getKey());
        Single result = scheduledUpdateEndpoint.loadUpdate(request);
        Assert.assertNull("ScheduledUpdate not found", result.blockingGet());
    }

    @Test
    public void loadUpdateTest() {
        Customer customer = (Customer) ModelInstanceFactory.createInstance(JsonUtils.load(CUSTOMER_JSON));
        LocalDate date = LocalDate.of(2018, 5, 10);
        ScheduledUpdate scheduledUpdate = new ScheduledUpdate(date, customer.getKey().getId().toString(), customer);
        scheduledUpdateRepository.save(resolveKeyspace(), scheduledUpdate);
        when(request.getCustomerKey()).thenReturn(customer.getKey());
        Single result = scheduledUpdateEndpoint.loadUpdate(request);
        Assert.assertNotNull("ScheduledUpdate not found", result.blockingGet());
    }

    private String resolveKeyspace() {
        DomainModel domainModel = ModelRepositoryFactory.getRepositoryFor(DomainModel.class)
                .getModel(modelResolver.getModelName(), modelResolver.getModelVersion());
        return ModeledEntitySchemaResolver.getSchemaNameUsing(domainModel, null);
    }
}
