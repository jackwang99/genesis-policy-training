/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.jobs.batch.handlers.opportunity;

import com.eisgroup.genesis.columnstore.ColumnStore;
import com.eisgroup.genesis.columnstore.statement.StatementBuilderFactory;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.repository.ModeledEntitySchemaResolver;
import com.eisgroup.genesis.jobs.lifecycle.api.handler.CommandCollectingHandler;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Dmitry Andronchik
 */
public abstract class BaseBatchOpportunityCommandHandler extends CommandCollectingHandler {
    
    protected static final String OPPORTUNITY_MODEL_NAME = "Opportunity";

    @Autowired
    protected ColumnStore columnStore;

    @Autowired
    protected StatementBuilderFactory statementBuilder;
    
    protected String getKeySpace() {
        return getKeySpace(OPPORTUNITY_MODEL_NAME);
    }
    
    protected String getKeySpace(String modelName) {
        DomainModel model = ModelRepositoryFactory.getRepositoryFor(DomainModel.class).getActiveModel(modelName);
        return ModeledEntitySchemaResolver.getSchemaNameUsing(model, getVariation());
    }

    public void setColumnStore(ColumnStore columnStore) {
        this.columnStore = columnStore;
    }

    public void setStatementBuilder(StatementBuilderFactory statementBuilder) {
        this.statementBuilder = statementBuilder;
    }

    protected abstract String getCommandNameToBeExecuted();
}
