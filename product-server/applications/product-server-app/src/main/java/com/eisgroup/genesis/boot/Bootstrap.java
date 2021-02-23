/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.boot;

import com.eisgroup.genesis.config.ProductServerConfig;
import com.eisgroup.genesis.config.SecurityConfig;
import com.eisgroup.genesis.facade.config.FacadeConfig;
import com.eisgroup.genesis.facade.security.config.FacadeSecurityConfig;
import com.eisgroup.genesis.pipeline.controller.PipelineController;
import com.eisgroup.genesis.product.specification.controller.ProductSpecificationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.esigroup.genesis.product.server.spring")
@SpringBootApplication(exclude = {
        HibernateJpaAutoConfiguration.class,
        SecurityAutoConfiguration.class,
        FacadeSecurityConfig.class,
        FacadeConfig.class
})
public class Bootstrap {

    public static void main(String[] args) {
        SpringApplication.run(
                new Class[]{
                        Bootstrap.class,
                        ProductServerConfig.class,
                        PipelineController.class,
                        ProductSpecificationController.class,
                        SecurityConfig.class
                },
                args);
    }
}
