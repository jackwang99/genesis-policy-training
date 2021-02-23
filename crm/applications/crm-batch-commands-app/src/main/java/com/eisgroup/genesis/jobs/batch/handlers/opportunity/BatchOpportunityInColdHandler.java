/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.jobs.batch.handlers.opportunity;

import com.eisgroup.genesis.crm.commands.CrmCommands;
import com.eisgroup.genesis.jobs.lifecycle.api.commands.output.SubsequentCommand;
import io.reactivex.Observable;
import org.springframework.beans.factory.annotation.Value;

/**
 * Checks opportunities that should be closed soon and notifies about this.
 *
 * @author Dmitry Andronchik
 */
public class BatchOpportunityInColdHandler extends BaseBatchOpportunityCloseHandler {

    public static final String NAME = "batchInCold";

    @Value("${genesis.job.crm.opportunityInColdDays}")
    protected Integer opportunityInColdDays;

    @Override
    public Observable<SubsequentCommand> execute() {
        return execute(opportunityInColdDays);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected String getCommandNameToBeExecuted() {
        return CrmCommands.IN_COLD;
    }

    public void setOpportunityInColdDays(Integer opportunityInColdDays) {
        this.opportunityInColdDays = opportunityInColdDays;
    }
}
