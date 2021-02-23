/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.json.reducer.ref.model;

import java.util.Optional;
import java.util.function.BiFunction;
import com.eisgroup.genesis.decision.DecisionService;
import com.eisgroup.genesis.decision.RowResult;
import com.eisgroup.genesis.decision.dimension.DecisionContext;
import com.eisgroup.genesis.decision.dsl.model.DecisionModel;
import com.eisgroup.genesis.json.reducer.ref.model.apects.AttributeTransformerAliasAspect;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import com.eisgroup.genesis.product.specification.traverser.TransformationInput;
import io.reactivex.Observable;
import org.apache.commons.lang3.StringUtils;

/**
 * Attribute transformer based on Decision table.
 *
 * @author ssauchuk
 * @since 10.2
 */
public class AttributeTransformerDecisionTable
        implements BiFunction<TransformationExecutionContext, TransformationInput, String> {

    protected static final String DECISION_MODEL_NAME = "AttributeTransformer";

    protected static final String DECISION_MODEL_VERSION = "1";

    protected static final String TABLE_NAME = "Attribute Name Transformation";

    protected static final String COMPONENT = "component";

    protected static final String ATTRIBUTE = "attribute";

    protected static final String TAG = "tag";

    private final DecisionService decisionService;

    public AttributeTransformerDecisionTable(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @Override
    public String apply(TransformationExecutionContext executionContext, TransformationInput transformationInput) {

        if (StringUtils.isEmpty(executionContext.getBehaviourValue())) {
            return transformationInput.getAttributeName();
        }

        Observable<RowResult> result = resolveAttributeAlias(executionContext, transformationInput);

        return Optional.ofNullable(result.blockingFirst(null))
                .map(rowResult -> (String) rowResult.get(AttributeTransformerAliasAspect.NAME))
                .orElse(transformationInput.getAttributeName());
    }

    protected Observable<RowResult> resolveAttributeAlias(TransformationExecutionContext executionContext,
            TransformationInput transformationInput) {

        DecisionContext decisionContext = resolveDecisionContext(executionContext, transformationInput);
        DecisionModel decisionModel = resolveDecisionModel();
        return decisionService.evaluateTable(TABLE_NAME, decisionModel, decisionContext);
    }

    protected DecisionModel resolveDecisionModel() {
        return ModelRepositoryFactory.getRepositoryFor(DecisionModel.class)
                .getModel(DECISION_MODEL_NAME, DECISION_MODEL_VERSION);
    }

    protected DecisionContext resolveDecisionContext(TransformationExecutionContext executionContext,
            TransformationInput transformationInput) {

        return DecisionContext.getBuilder().addParameter(COMPONENT, transformationInput.getEntityDesignName())
                .addParameter(ATTRIBUTE, transformationInput.getAttributeName())
                .addParameter(TAG, executionContext.getBehaviourValue()).build();
    }
}
