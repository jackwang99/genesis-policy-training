/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.lifecycle.services.operations.impl;

import java.util.List;

import com.eisgroup.genesis.comparison.representation.DiffEntity;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;
import com.eisgroup.genesis.json.link.EntityLinkBuilderRegistry;
import com.eisgroup.genesis.policy.core.services.operations.TransactionalOperationHistoryRecordCreator;

/**
 * History record creator for complete offer command handler.
 *
 * @author yratkevich
 * @since 9.16
 */
public class CompleteOfferOperationHistoryRecordCreator<E extends PolicySummary> extends TransactionalOperationHistoryRecordCreator<E> {
    
    public static final String COMPLETE_OFFER = "completeOffer";

    public CompleteOfferOperationHistoryRecordCreator(EntityLinkBuilderRegistry entityLinkBuilderRegistry) {
        super(entityLinkBuilderRegistry);
    }

    @Override
    public Boolean isApplicable(RootEntity changedEntity, String operationName, List<DiffEntity> diffs) {
        return COMPLETE_OFFER.equals(operationName);
    }
}