/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.jobs.batch.lifecycle.module;

import com.eisgroup.genesis.jobs.batch.handlers.BatchBirthdayCommunicationHandler;
import com.eisgroup.genesis.jobs.batch.handlers.BatchScheduledUpdateHandler;
import com.eisgroup.genesis.jobs.batch.handlers.campaign.*;
import com.eisgroup.genesis.jobs.batch.handlers.opportunity.BatchCloseHandler;
import com.eisgroup.genesis.jobs.batch.handlers.opportunity.BatchOpportunityCreationHandler;
import com.eisgroup.genesis.jobs.batch.handlers.opportunity.BatchOpportunityInColdHandler;
import com.eisgroup.genesis.jobs.lifecycle.api.module.AbstractJobLifecycleModule;
import com.eisgroup.genesis.lifecycle.CommandHandler;

import java.util.HashMap;
import java.util.Map;


/**
 * Batch job module configurator
 *
 * @author kbublys
 */
public class JobsBatchLifecycleModule extends AbstractJobLifecycleModule {
    
    @Override
    public String getModelName() {
        return "crmJobs";
    }    

    @Override
    public Map<String, CommandHandler<?, ?>> getBatchCommandHandlers() {
        Map<String, CommandHandler<?, ?>> handlers = new HashMap<>();
        handlers.put(BatchCloseHandler.NAME, new BatchCloseHandler());
        handlers.put(BatchBirthdayCommunicationHandler.NAME, new BatchBirthdayCommunicationHandler());
        handlers.put(BatchOpportunityCreationHandler.NAME, new BatchOpportunityCreationHandler());
        handlers.put(BatchOpportunityInColdHandler.NAME, new BatchOpportunityInColdHandler());
        handlers.put(BatchScheduledUpdateHandler.NAME, new BatchScheduledUpdateHandler());
        handlers.put(BatchStartCampaignHandler.NAME, new BatchStartCampaignHandler());
        handlers.put(BatchRestartCampaignHandler.NAME, new BatchRestartCampaignHandler());
        handlers.put(BatchSuspendCampaignHandler.NAME, new BatchSuspendCampaignHandler());
        handlers.put(BatchCompleteCampaignHandler.NAME, new BatchCompleteCampaignHandler());
        handlers.put(BatchArchiveCampaignHandler.NAME, new BatchArchiveCampaignHandler());
        return handlers;
    }
}
