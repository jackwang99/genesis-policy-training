/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.container.config;

import com.eisgroup.genesis.crm.container.search.AgencyContainerSearchSchemaProviderPlugin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import com.eisgroup.genesis.crm.container.search.AgencyContainerIndexPlugin;
import com.eisgroup.genesis.crm.core.service.AgencyContainerService;
import com.eisgroup.genesis.search.SearchIndexStatementFactory;


@ConditionalOnClass(name = "com.eisgroup.genesis.search.plugin.ModeledSearchIndexPlugin")
public class ContainerSearchIntegrationConfig {

    @Bean
    public AgencyContainerIndexPlugin individualAgencyContainerIndexPlugin(SearchIndexStatementFactory statementFactory,
                    AgencyContainerService agencyContainerService) {
        return new AgencyContainerIndexPlugin("INDIVIDUALCUSTOMER", "IndividualAgencyContainer", statementFactory, agencyContainerService);
    }
    
    @Bean
    public AgencyContainerIndexPlugin organizationAgencyContainerIndexPlugin(SearchIndexStatementFactory statementFactory,  
                    AgencyContainerService agencyContainerService) {
        return new AgencyContainerIndexPlugin("ORGANIZATIONCUSTOMER", "OrganizationAgencyContainer", statementFactory, agencyContainerService);
    }

    @Bean
    public AgencyContainerSearchSchemaProviderPlugin individualAgencyContainerSearchSchemaProviderPlugin() {
        return new AgencyContainerSearchSchemaProviderPlugin("INDIVIDUALCUSTOMER", "IndividualAgencyContainer");
    }

    @Bean
    public AgencyContainerSearchSchemaProviderPlugin organizationAgencyContainerSearchSchemaProviderPlugin() {
        return new AgencyContainerSearchSchemaProviderPlugin("ORGANIZATIONCUSTOMER", "OrganizationAgencyContainer");
    }
}
