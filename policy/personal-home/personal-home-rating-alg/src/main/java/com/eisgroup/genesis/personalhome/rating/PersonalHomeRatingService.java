/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.personalhome.rating;

import com.eisgroup.rating.output.impl.DefaultRatingResult;
import org.openl.rules.ruleservice.core.annotations.ServiceExtraMethod;

/**
 * Interface that will be exposed when deploying OpenL Personal Home rules
 *
 * @author zhchen
 * @since 1.0
 */
public interface PersonalHomeRatingService {

    @ServiceExtraMethod(PersonalHomeServiceExtraMethodHandler.class)
    DefaultRatingResult rate(PersonalHomeRatingRequest request);
}
