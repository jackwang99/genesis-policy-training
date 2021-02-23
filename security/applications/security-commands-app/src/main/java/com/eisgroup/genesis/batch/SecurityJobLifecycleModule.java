/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.batch;

import com.eisgroup.genesis.jobs.lifecycle.api.module.AbstractJobLifecycleModule;
import com.eisgroup.genesis.lifecycle.CommandHandler;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * A definition for the security job lifecycle module.
 * Currently contains a single POC handler that creates BAM events for users.
 *
 * @author gvisokinskas
 */
public class SecurityJobLifecycleModule extends AbstractJobLifecycleModule {
    @Override
    public Map<String, CommandHandler<?, ?>> getBatchCommandHandlers() {
        return ImmutableMap.of(BamCreateJob.NAME, new BamCreateJob());
    }
}
