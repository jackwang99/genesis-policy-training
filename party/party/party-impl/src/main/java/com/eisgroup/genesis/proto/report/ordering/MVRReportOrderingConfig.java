/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.proto.report.ordering;

import com.eisgroup.genesis.registry.report.ordering.ReportOrderingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring bean configuration for MVR Report.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
@Configuration
public class MVRReportOrderingConfig {

    @Bean
    @ConditionalOnMissingBean(name = "mvrReportOrderingService")
    public ReportOrderingService mvrReportOrderingService() {
        return new MVRReportOrderingService();
    }

}
