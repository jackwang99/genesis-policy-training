/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.jobs.batch.handlers.opportunity;

import com.eisgroup.genesis.crm.commands.CrmCommands;
import com.eisgroup.genesis.jobs.lifecycle.api.commands.output.SubsequentCommand;
import io.reactivex.Observable;
import org.springframework.beans.factory.annotation.Value;

/**
 * 
 * @author Dmitry Andronchik
 */
public class BatchCloseHandler extends BaseBatchOpportunityCloseHandler {
    
    public static final String NAME = "batchClose";
    
    @Value("${genesis.job.crm.opportunityCloseDays}")
    protected Integer opportunityCloseDays;    
    
    @Override
    public Observable<SubsequentCommand> execute() {
        return execute(opportunityCloseDays);
    }
    
    @Override
    public String getName() {
        return NAME;
    }
    
    @Override
    protected String getCommandNameToBeExecuted() {
        return CrmCommands.CLOSE;
    }
    
    public void setOpportunityCloseDays(Integer opportunityCloseDays) {
        this.opportunityCloseDays = opportunityCloseDays;
    }    
}
