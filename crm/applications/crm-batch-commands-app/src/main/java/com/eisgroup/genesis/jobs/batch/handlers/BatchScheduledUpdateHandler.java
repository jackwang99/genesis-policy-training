/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.jobs.batch.handlers;

import com.eisgroup.genesis.crm.commands.CrmCommands;
import com.eisgroup.genesis.crm.commands.request.ScheduledUpdateApplyRequest;
import com.eisgroup.genesis.crm.repository.impl.ScheduledUpdateRepository;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.repository.ModeledEntitySchemaResolver;
import com.eisgroup.genesis.jobs.lifecycle.api.commands.output.SubsequentCommand;
import com.eisgroup.genesis.jobs.lifecycle.api.handler.CommandCollectingHandler;
import com.eisgroup.genesis.model.Variation;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

/**
 * @author dlevchuk
 */
public class BatchScheduledUpdateHandler extends CommandCollectingHandler {

    public static final String NAME = "batchScheduledCustomerUpdate";

    private static final String INDIVIDUAL_MODEL_NAME = "INDIVIDUALCUSTOMER";
    private static final String ORGANIZATION_MODEL_NAME = "ORGANIZATIONCUSTOMER";

    @Autowired
    private ScheduledUpdateRepository scheduledUpdateRepository;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected Observable<SubsequentCommand> execute() {
        return commands(INDIVIDUAL_MODEL_NAME)
                .mergeWith(commands(ORGANIZATION_MODEL_NAME));
    }

    private Observable<SubsequentCommand> commands(String modelName) {
        return scheduledUpdateRepository.fetch(getKeySpace(modelName), LocalDate.now())
                .map(scheduledUpdate -> new SubsequentCommand(CrmCommands.APPLY_SCHEDULED_UPDATE,
                                                              new ScheduledUpdateApplyRequest(scheduledUpdate.getCustomer().getKey().getId().toString()),
                                                              Variation.INVARIANT,
                                                              modelName));
    }

    private String getKeySpace(String modelName) {
        DomainModel model = ModelRepositoryFactory.getRepositoryFor(DomainModel.class).getActiveModel(modelName);
        return ModeledEntitySchemaResolver.getSchemaNameUsing(model, getVariation());
    }
}
