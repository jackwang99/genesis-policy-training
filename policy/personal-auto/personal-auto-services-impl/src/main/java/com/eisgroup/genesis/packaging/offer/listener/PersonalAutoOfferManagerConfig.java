/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.packaging.offer.listener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.eisgroup.genesis.packaging.applicability.ComponentApplicabilityValidateServiceImpl;
import com.eisgroup.genesis.packaging.applicability.PersonalAutoComponentApplicabilityValidateServiceImpl;
import com.eisgroup.genesis.packaging.context.PackageContext;


/**
 * Offer Manager configuration
 * @author aspichakou
 * @since 1.0
 */
@Configuration
public class PersonalAutoOfferManagerConfig {
	
    @Bean
    public GrandfatherOfferManagerListener grandfatherOfferManagerListener() {
        return new GrandfatherOfferManagerListener();
    }
    
    @Bean
    @Primary
    public ComponentApplicabilityValidateServiceImpl<PackageContext> componentApplicablityValidateService(){
        return new PersonalAutoComponentApplicabilityValidateServiceImpl<>();
    }
    
    @Bean
    public GfCoverageCollector gfCoverageCollector() {
        return new GfCoverageCollector();
    }
    
    @Bean
    public GfCoveragePusher gfCoveragePusher() {
        return new GfCoveragePusher();
    }
    
}
