/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote;

import com.eisgroup.genesis.commands.request.IdentifierRequest;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.policy.core.lifecycle.commands.quote.QuoteCommandHandler;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.PersonalAutoCommands;
import io.reactivex.Single;

/**
 * Handler for changing state of a quote to `readyForOffer`.
 *
 * @author yratkevich
 * @since 9.16
 */
public class CompleteDataGatherHandler extends QuoteCommandHandler<IdentifierRequest, PolicySummary> {

    @Override
    public String getName() {
        return PersonalAutoCommands.COMPLETE_DATA_GATHER;
    }

    @Override
    public Single<PolicySummary> execute(IdentifierRequest identifierRequest, PolicySummary entity) {
        return Single.just(entity);
    }
}