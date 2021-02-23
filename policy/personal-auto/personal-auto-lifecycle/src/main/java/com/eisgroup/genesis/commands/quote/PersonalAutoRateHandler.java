/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.commands.quote;

import com.eisgroup.genesis.factory.model.personalauto.AutoLOB;
import com.eisgroup.genesis.factory.model.personalauto.PersonalAutoPolicySummary;
import com.eisgroup.genesis.factory.modeling.types.VehicleRiskItem;
import com.eisgroup.genesis.factory.modeling.types.immutable.PersonalAutoLOB;
import com.eisgroup.genesis.policy.core.rating.repository.model.RateAggregate;
import com.eisgroup.genesis.policy.core.rating.repository.model.RateEntry;
import com.eisgroup.genesis.policy.core.rating.services.rateable.RatingUtils;
import com.eisgroup.genesis.policy.core.rating.services.visitor.PremiumHolderInfo;
import com.eisgroup.genesis.policy.core.rating.services.visitor.PremiumHolderVisitor;
import com.eisgroup.genesis.policy.pnc.auto.lifecycle.commands.quote.AutoRateHandler;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Personal Auto rater
 *
 * @author aspichakou
 * @since 1.0
 */
public class PersonalAutoRateHandler extends AutoRateHandler<PersonalAutoPolicySummary> {

	@Autowired
    private PremiumHolderVisitor<PremiumHolderInfo> premiumHolderVisitor;

    @Override
    protected Single<PersonalAutoPolicySummary> calculatePremiums(PersonalAutoPolicySummary quote) {

        final List<List<VehicleRiskItem>> initialRiskItems = new ArrayList<>();
        if (quote.getBlob() != null) {
            final Collection<PersonalAutoLOB> lobs = quote.getBlob().getLobs();
            if (lobs != null) {
                // Collect initial list of RIs
                initialRiskItems.addAll(lobs.stream()
                        .map(lob -> lob.getRiskItems().stream()
                                .map(riskItem -> (VehicleRiskItem) riskItem)
                                .collect(Collectors.toList())
                        )
                        .collect(Collectors.toList())
                );
            }
        }

        final Single<PersonalAutoPolicySummary> processedQuote = super.calculatePremiums(quote);

        // Re-apply RIs
        return processedQuote.map(processedQuoteResult -> {
            final Collection<PersonalAutoLOB> processedLobs = processedQuoteResult.getBlob().getLobs();
            
            if(processedLobs != null) {
                int i = 0;
                for (PersonalAutoLOB lob : processedLobs) {
                    final List<VehicleRiskItem> list = initialRiskItems.get(i);
                    ((AutoLOB) lob).setRiskItems(list);
                }
            }
            return processedQuoteResult;            
        });
    }

    @Override
    protected Observable<RateAggregate> calculateRates(PersonalAutoPolicySummary policySummary) {
        final Map<UUID, PremiumHolderInfo> collect = premiumHolderVisitor.visit(policySummary, policySummary.getModelName()).stream().collect(Collectors.toMap(r -> r.getPremiumHolderKey().getId(), r -> r));
        return super.calculateRates(policySummary)
                .map(r -> {
                    collect.remove(r.getKey().getPremiumHolderId());
                    return r;
                })
                .mergeWith(Observable.fromIterable(() -> collect.values().stream()
                        .map(premiumHolderInfo -> {
                            RateEntry rateEntry = RatingUtils.createBaseRateEntry(Money.of(BigDecimal.ZERO, policySummary.getCurrencyCd()));
                            RateAggregate ra = RatingUtils.createRateAggregate(premiumHolderInfo);
                            ra.getEntries().add(rateEntry);
                            return ra;
                        })
                        .collect(Collectors.toList()).iterator())
                );
    } 
}