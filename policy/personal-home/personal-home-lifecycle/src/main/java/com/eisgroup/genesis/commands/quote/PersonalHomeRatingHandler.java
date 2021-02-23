/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.commands.quote;


import com.eisgroup.genesis.factory.model.personalhome.PersonalHomePolicySummary;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.policy.core.lifecycle.commands.quote.RateHandler;
import com.eisgroup.genesis.policy.core.rating.repository.model.RateAggregate;
import com.eisgroup.genesis.policy.core.rating.services.RatingService;
import io.reactivex.Observable;
import org.springframework.beans.factory.annotation.Autowired;

public class PersonalHomeRatingHandler extends RateHandler {

    @Autowired
    private RatingService<PersonalHomePolicySummary> homeRatingService;

    @Override
    protected Observable<RateAggregate> calculateRates(PolicySummary quote) {
        return Observable.fromCallable(() -> homeRatingService.rate((PersonalHomePolicySummary) quote))
                .flatMap(Observable::fromIterable);
    }
}