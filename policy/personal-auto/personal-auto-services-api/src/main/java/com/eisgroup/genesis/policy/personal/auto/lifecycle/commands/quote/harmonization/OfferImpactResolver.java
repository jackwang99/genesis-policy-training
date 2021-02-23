/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization;

import java.util.List;

import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import io.reactivex.Single;

/**
 * Resolve attribute diffs (i.e. using DT) and returns resolved Quote.
 *
 * @author yratkevich
 * @since 9.16
 */
public interface OfferImpactResolver {

    Single<PolicySummary> applyDiffs(List<OfferImpactDiffHolder> attributeDiffs, PolicySummary targetQuote);
}