/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.core.premiums.configurations.impl;

import com.eisgroup.genesis.policy.core.premiums.api.rules.rounding.PremiumRoundingRule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto Product specific custom premium configurations.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
@Configuration
public class CustomizedPremiumConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "biCoveragePremiumRoundingRule")
    public PremiumRoundingRule biCoveragePremiumRoundingRule() {
        return new BiCoveragePremiumRoundingRule();
    }

}
