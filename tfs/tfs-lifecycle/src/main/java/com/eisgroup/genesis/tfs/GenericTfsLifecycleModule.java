/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.tfs;

import com.eisgroup.genesis.tfs.lifecycle.TfsLifecycleModule;
import com.eisgroup.genesis.tfs.services.config.TfsServicesConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * TFS lifecycle module configuration
 *
 * @author aarsatyants
 * @since 2019M1
 */

@Configuration
@ComponentScan("com.eisgroup.genesis.tfs.lifecycle.commands.events.handlers")
@Import(TfsServicesConfig.class)
public class GenericTfsLifecycleModule extends TfsLifecycleModule {

    public static final String MODEL_NAME = "TFS.Generic";
    private static final String VERSION = "1";

    @Override
    public Object[] getConfigResources() {
        return ArrayUtils.addAll(super.getConfigResources(),
                TfsLifecycleModuleConfig.class);
    }

    @Override
    public String getModelName() {
        return MODEL_NAME;
    }

    @Override
    public String getModelVersion() {
        return VERSION;
    }
}
