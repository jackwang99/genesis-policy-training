package com.eisgroup.genesis.tfs;

import com.eisgroup.genesis.spring.delegate.DelegatingBean;
import com.eisgroup.genesis.tfs.lifecycle.TfsLifecycleModule;
import com.eisgroup.genesis.tfs.services.TfsUnbilledTaxServiceImpl;
import com.eisgroup.genesis.tfs.services.api.TfsPolicyPremiumChangeProcessingService;
import com.eisgroup.genesis.tfs.services.api.TfsUnbilledTaxService;
import com.exigen.genesis.tfs.proto.services.TfsPolicyPremiumChangeProcessingServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TfsLifecycleModuleConfig {

    @Bean
    @ConditionalOnMissingBean(ignored = DelegatingBean.class)
    public TfsUnbilledTaxService tfsUnbilledTaxService() {
        return new TfsUnbilledTaxServiceImpl();
    }

    @Bean
    public TfsPolicyPremiumChangeProcessingService tfsPolicyPremiumChangeProcessingService() {
        return new TfsPolicyPremiumChangeProcessingServiceImpl();
    }
}
