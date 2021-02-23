/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization;

import java.util.List;

import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yratkevich
 * @since 9.16
 */
public class NoOpOfferImpactResolver implements OfferImpactResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpOfferImpactResolver.class);

    @Override
    public Single<PolicySummary> applyDiffs(List<OfferImpactDiffHolder> attributeDiffs, PolicySummary targetQuote) {
        LOGGER.warn("Applying diffs without any operations");
        return Single.just(targetQuote);
    }
}