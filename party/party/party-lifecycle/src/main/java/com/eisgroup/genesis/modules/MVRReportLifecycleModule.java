/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.modules;

import com.eisgroup.genesis.lifecycle.CommandHandler;
import com.eisgroup.genesis.registry.report.commands.MVRReportOrderingHandler;
import com.eisgroup.genesis.registry.report.constant.ReportRegistryConstants;
import com.eisgroup.genesis.registry.report.modules.AbstractMVRReportCoreLifecycleModule;

import java.util.Map;

/**
 * MVR Report Lifecycle module implementation.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class MVRReportLifecycleModule extends AbstractMVRReportCoreLifecycleModule {

    @Override
    public String getModelName() {
        return ReportRegistryConstants.MVR_REPORT;
    }

    @Override
    public Map<String, CommandHandler<?, ?>> getCommandHandlers() {
        Map<String, CommandHandler<?, ?>> commands = super.getCommandHandlers();
        commands.put(MVRReportOrderingHandler.NAME, new MVRReportOrderingHandler());

        return commands;
    }

}
