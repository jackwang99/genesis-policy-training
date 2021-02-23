/*
 * Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.individual.facade.itests.config;

import com.eisgroup.genesis.columnstore.ColumnStore;
import com.eisgroup.genesis.columnstore.statement.StatementBuilderFactory;
import com.eisgroup.genesis.crm.repository.impl.ScheduledUpdateRepository;
import com.eisgroup.genesis.facade.ScheduledUpdateEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author dlevchuk
 */
@Configuration
@Import({CrmTestConfig.class})
public class ScheduledUpdateTestConfig {

    @Bean
    public ScheduledUpdateRepository scheduledUpdateRepository(ColumnStore columnStore, StatementBuilderFactory statementBuilderFactory) {
        return new ScheduledUpdateRepository(columnStore, statementBuilderFactory);
    }

    @Bean
    public ScheduledUpdateEndpoint scheduledUpdateEndpoint() {
        return new ScheduledUpdateEndpoint();
    }
}
