/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.commands.crm;

import com.eisgroup.genesis.columnstore.InMemoryColumnStoreConfig;
import com.eisgroup.genesis.commands.crm.config.ScheduledUpdateTestConfig;
import com.eisgroup.genesis.comparison.impl.config.ComparisonConfig;
import com.eisgroup.genesis.crm.entity.ScheduledUpdate;
import com.eisgroup.genesis.crm.repository.config.CustomerReadRepositoryConfig;
import com.eisgroup.genesis.crm.repository.config.CustomerRepositoryConfig;
import com.eisgroup.genesis.crm.repository.config.CustomerWriteRepositoryConfig;
import com.eisgroup.genesis.crm.repository.impl.ScheduledUpdateRepository;
import com.eisgroup.genesis.crm.rules.validation.config.CrmKrakenValidationServiceConfig;
import com.eisgroup.genesis.decision.dimension.config.DecisionDimensionConfig;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.eisgroup.genesis.factory.repository.ModeledEntitySchemaResolver;
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
import io.reactivex.Single;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.time.LocalDate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author dlevchuk
 */
@ContextConfiguration(classes = {
        InMemoryColumnStoreConfig.class,
        ModeledEntityColumnStoreConfig.class,
        ScheduledUpdateTestConfig.class,
        KrakenRepositoryRegistryConfig.class,
        KrakenValidationServiceConfig.class,
        CrmKrakenValidationServiceConfig.class,
        DecisionDimensionConfig.class,
        EntityLinkResolverSpringConifg.class,
        EntityLinkBuilderSpringConifg.class,
        CustomerReadRepositoryConfig.class,
        CustomerWriteRepositoryConfig.class,
        CustomerRepositoryConfig.class,
        ComparisonConfig.class,
        SearchIndexSpringConfig.class        
})
@Category(IntegrationTests.class)
public class ScheduledUpdateRepositoryTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private ScheduledUpdateRepository scheduledUpdateRepository;

    @Autowired
    private ModelResolver modelResolver;

    @Test
    public void testSave() {
        Customer customer = (Customer)ModelInstanceFactory.createRootInstance("INDIVIDUALCUSTOMER", "1");
        RootEntityKey.generateInitialKey(customer.getKey());
        customer.setCustomerNumber("Denis");

        LocalDate date = LocalDate.of(2018, 1, 1);

        ScheduledUpdate scheduledUpdate = new ScheduledUpdate(date, customer.getKey().getId().toString(), customer);
        scheduledUpdateRepository.save(resolveKeyspace(), scheduledUpdate);

        scheduledUpdateRepository.fetch(resolveKeyspace(), date)
                .test()
                .assertResult(scheduledUpdate);
    }

    @Test
    public void testDelete() {
        Customer customer = (Customer)ModelInstanceFactory.createRootInstance("INDIVIDUALCUSTOMER", "1");
        RootEntityKey.generateInitialKey(customer.getKey());
        customer.setCustomerNumber("001");

        LocalDate date = LocalDate.of(2019, 1, 1);

        ScheduledUpdate scheduledUpdate = new ScheduledUpdate(date, customer.getKey().getId().toString(), customer);
        scheduledUpdateRepository.save(resolveKeyspace(), scheduledUpdate);

        scheduledUpdateRepository.fetch(resolveKeyspace(), date)
                .test()
                .assertResult(scheduledUpdate);

        scheduledUpdateRepository.delete(resolveKeyspace(), customer.getKey().getId().toString());
        assertTrue(scheduledUpdateRepository.fetch(resolveKeyspace(), date).isEmpty().blockingGet());
    }

    @Test
    public void testEmpty() {
        Single<String> res = scheduledUpdateRepository.fetch(resolveKeyspace(), "1111")
                .isEmpty()
                .flatMap(empty -> {
                    if (empty) {
                        return Single.just("empty");
                    } else {
                        return Single.just("notEmpty");
                    }
                });
        res.subscribe((su) -> System.out.println(su));
    }

    private String resolveKeyspace() {
        DomainModel domainModel = ModelRepositoryFactory.getRepositoryFor(DomainModel.class)
                .getModel(modelResolver.getModelName(), modelResolver.getModelVersion());
        return ModeledEntitySchemaResolver.getSchemaNameUsing(domainModel, null);
    }
}
