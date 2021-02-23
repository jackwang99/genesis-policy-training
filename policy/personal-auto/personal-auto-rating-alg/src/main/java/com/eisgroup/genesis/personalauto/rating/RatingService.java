/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.personalauto.rating;


import com.eisgroup.genesis.tfs.services.api.impl.DefaultTfsCalculationResult;
import com.eisgroup.rating.output.impl.DefaultRatingResult;
import org.openl.rules.ruleservice.core.annotations.ServiceExtraMethod;

/**
 * Interface that will be exposed when deploying OpenL Personal Auto rules
 *
 * @author Denis Levchuk on 8/2/17.
 */
public interface RatingService {

    @ServiceExtraMethod(ServiceExtraMethodHandlerImpl.class)
    DefaultRatingResult rate(RatingRequest request);

    @ServiceExtraMethod(TFServiceExtraMethodHandlerImpl.class)
    DefaultTfsCalculationResult calculateTaxesAndFees(RatingRequest request);


}
