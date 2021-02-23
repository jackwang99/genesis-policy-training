/*
 * Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.individual.facade.itests.config;

import com.eisgroup.genesis.bam.events.ActivityEventPublisher;
import com.eisgroup.genesis.bam.events.ActivityStreamEventBuilderFactory;
import com.eisgroup.genesis.command.result.CommandResult;
import com.eisgroup.genesis.command.result.CommandSuccess;
import com.eisgroup.genesis.commands.publisher.api.CommandPublisher;
import com.eisgroup.genesis.commands.services.CrmValidationService;
import com.eisgroup.genesis.commands.validator.CommandRequestValidator;
import com.eisgroup.genesis.crm.repository.CrmWriteRepository;
import com.eisgroup.genesis.crm.repository.impl.DefaultCustomerWriteRepository;
import com.eisgroup.genesis.factory.model.lifecycle.Command;
import com.eisgroup.genesis.factory.services.AccessTrackInfoService;
import com.eisgroup.genesis.factory.services.ProductProvider;
import com.eisgroup.genesis.json.JsonEntity;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.modules.CommandDispatcher;
import com.eisgroup.genesis.validator.EntityValidatorRegistry;
import com.eisgroup.genesis.versioning.VersioningReadRepository;
import io.reactivex.Maybe;
import io.reactivex.Single;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

/**
 * Common spring test config for the CRM Individual Customer model
 *
 * @author dlevchuk
 */
public class CrmTestConfig {

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
    public CommandDispatcher commandDispatcher() {
        return new MockCommandDispatcher();
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

    @Bean(name="customerWriteRepository")
    public CrmWriteRepository mockCrmWriteRepository() {
        return new DefaultCustomerWriteRepository();
    }

    @Bean
    public VersioningReadRepository mockVersioningReadRepository() {
        return mock(VersioningReadRepository.class);
    }

    @Bean
    public CommandPublisher mockCommandPublisher() {
        return mock(CommandPublisher.class);
    }

    @Bean
    public CommandRequestValidator commandRequestValidator() {
        return new MockCommandRequestValidator(null);
    }

    public static class MockCommandRequestValidator extends CommandRequestValidator {
        public MockCommandRequestValidator(EntityValidatorRegistry validatorRegistry) {
            super(validatorRegistry);
        }

        @Override
        public Maybe<CommandResult> validate(String modelName, Command command, JsonEntity entity) {
            return Maybe.empty();
        }
    }

    public static class MockCommandDispatcher extends CommandDispatcher {
        @Override
        public Single<CommandResult> execute(com.eisgroup.genesis.command.Command command) {
            return Single.just(new CommandSuccess(command.getData().getAsJsonObject().get("customer")));
        }
    }
}
