/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.conversion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eisgroup.genesis.conversion.ConversionCrmConstants.ModelName;
import com.eisgroup.genesis.crm.repository.impl.ModelAwareNumberGenerator;
import com.eisgroup.genesis.generator.EntityNumberGenerator;
import com.eisgroup.genesis.generator.SequenceGenerator;
import com.eisgroup.genesis.generator.impl.SimpleNumberGenerator;

@Configuration
public class ConversionCrmWriteConfig {

    @Bean
    public EntityNumberGenerator customerAccountNumberGenerator(SequenceGenerator sequenceGenerator) {
        return new SimpleNumberGenerator("customer_account_number", "A%010d", sequenceGenerator);
    }

    @Bean
    public EntityNumberGenerator communicationIdGenerator(SequenceGenerator sequenceGenerator) {
        return new SimpleNumberGenerator("communication_id", "C%010d", sequenceGenerator);
    }

    @Bean
    public EntityNumberGenerator customerIndividualNumberGenerator(SequenceGenerator sequenceGenerator) {
        return new ModelAwareNumberGenerator(ModelName.INDIVIDUAL_CUSTOMER, "customer_number", "IC", "%010d",
                sequenceGenerator);
    }

    @Bean
    public EntityNumberGenerator customerOrganizationNumberGenerator(SequenceGenerator sequenceGenerator) {
        return new ModelAwareNumberGenerator(ModelName.ORGANIZATION_CUSTOMER, "customer_number", "OC", "%010d",
                sequenceGenerator);
    }

    @Bean
    public EntityNumberGenerator opportunityIdGenerator(SequenceGenerator sequenceGenerator) {
        return new SimpleNumberGenerator("opportunity_id", "OP%010d", sequenceGenerator);
    }

    @Bean
    public EntityNumberGenerator majorAccountIdGenerator(SequenceGenerator sequenceGenerator) {
        return new SimpleNumberGenerator("major_account_id", "MA%010d", sequenceGenerator);
    }
}
