/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.modules;

import java.util.HashMap;
import java.util.Map;

import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.AbstractStrategyJob;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.ArchivePolicyJob;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.BulkBORTransferJob;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.DeleteArchivedPolicyJob;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.ExpirationQuoteJob;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.endorsement.EndorsementInitiationJob;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.endorsement.EndorsementIssueJob;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.endorsement.EndorsementProposingJob;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.endorsement.EndorsementRateJob;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.quote.QuoteCommonJob;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.renewal.RenewalInitializationJob;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.renewal.RenewalIssueJob;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.renewal.RenewalProposingJob;
import com.eisgroup.genesis.batch.commands.handlers.autoprocessing.renewal.RenewalRateJob;
import com.eisgroup.genesis.jobs.lifecycle.api.module.AbstractJobLifecycleModule;
import com.eisgroup.genesis.lifecycle.CommandHandler;

/**
 * Batch job module configurator
 *
 * @author kbublys
 * @since 1.0
 */
public class PolicyBatchCommandsLifecycleModule extends AbstractJobLifecycleModule {

    @Override
    public String getModelName() {
        return "policyJobs";
    }

    @Override
    public Map<String, CommandHandler<?, ?>> getBatchCommandHandlers() {
        Map<String, CommandHandler<?, ?>> handlers = new HashMap<>();

        /* Renewal jobs */
        addJob(handlers, new RenewalInitializationJob());
        addJob(handlers, new RenewalIssueJob());
        addJob(handlers, new RenewalProposingJob());
        addJob(handlers, new RenewalRateJob());

        /* Endorsement jobs */
        addJob(handlers, new EndorsementInitiationJob());
        addJob(handlers, new EndorsementIssueJob());
        addJob(handlers, new EndorsementProposingJob());
        addJob(handlers, new EndorsementRateJob());

        addJob(handlers, new ArchivePolicyJob());
        addJob(handlers, new BulkBORTransferJob());
        addJob(handlers, new DeleteArchivedPolicyJob());
        addJob(handlers, new ExpirationQuoteJob());

        addJob(handlers, new QuoteCommonJob());

        return handlers;
    }

    private void addJob(Map<String, CommandHandler<?, ?>> handlers, AbstractStrategyJob job) {
        handlers.put(job.getName(), job);
    }
}