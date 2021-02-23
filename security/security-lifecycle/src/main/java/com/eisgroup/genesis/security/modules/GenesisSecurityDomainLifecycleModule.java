/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.security.modules;

import org.springframework.context.annotation.Configuration;

/**
 * Defines prototyped version of security domain lifecycle module
 *
 * @author alizdenis
 */
@Configuration
public class GenesisSecurityDomainLifecycleModule extends SecurityDomainLifecycleModule {

    @Override
    public String getModelName() {
        return "Security";
    }

}
