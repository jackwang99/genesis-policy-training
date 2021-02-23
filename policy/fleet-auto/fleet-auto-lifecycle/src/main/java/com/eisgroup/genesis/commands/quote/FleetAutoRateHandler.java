/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.commands.quote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.eisgroup.genesis.policy.core.rating.services.tfs.PolicySummaryWrapper;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;

import com.eisgroup.genesis.factory.model.fleetauto.FleetAutoPolicySummary;
import com.eisgroup.genesis.policy.core.rating.repository.model.RateAggregate;
import com.eisgroup.genesis.policy.core.rating.repository.model.RateEntry;
import com.eisgroup.genesis.policy.core.rating.services.RatingService;
import com.eisgroup.genesis.policy.core.rating.services.rateable.RatingUtils;
import com.eisgroup.genesis.policy.core.rating.services.visitor.PremiumHolderInfo;
import com.eisgroup.genesis.policy.core.rating.services.visitor.PremiumHolderVisitor;
import com.eisgroup.genesis.policy.pnc.auto.lifecycle.commands.quote.AutoRateHandler;

import io.reactivex.Observable;

/**
 * Fleet Auto rate handler
 * 
 * @author aspichakou
 * @since 1.0
 */
public class FleetAutoRateHandler extends AutoRateHandler<FleetAutoPolicySummary> {

    @Autowired
    private RatingService<FleetAutoPolicySummary> fleetRatingService;

    @Autowired
    private PremiumHolderVisitor<PremiumHolderInfo> premiumHolderVisitor;

    @Override
    protected Observable<RateAggregate> calculateRates(FleetAutoPolicySummary policySummary) {

        final List<PremiumHolderInfo> premiumHolders = premiumHolderVisitor.visit(policySummary, policySummary.getModelName());

        final List<RateAggregate> realRates = fleetRatingService.rate(policySummary);

        if (!realRates.isEmpty()){
            fleetRatingService.calculateTaxesAndFees(new PolicySummaryWrapper<>(policySummary,realRates));
        }

        final List<RateAggregate> rateAggregates = new ArrayList<>(realRates);

        final Map<UUID, RateAggregate> realRatesMap = realRates.stream()
                .collect(Collectors.toMap(r -> r.getKey().getPremiumHolderId(), r -> r));

        for (PremiumHolderInfo p : premiumHolders) {
            if (!realRatesMap.containsKey(p.getPremiumHolderKey().getId())) {

                final RateAggregate ra = RatingUtils.createRateAggregate(p);
                rateAggregates.add(ra);

                final RateEntry re = RatingUtils.createBaseRateEntry(Money.of(352, policySummary.getCurrencyCd()));
                ra.getEntries().add(re);
            }            
        }

        return Observable.fromIterable(rateAggregates);
    }
}