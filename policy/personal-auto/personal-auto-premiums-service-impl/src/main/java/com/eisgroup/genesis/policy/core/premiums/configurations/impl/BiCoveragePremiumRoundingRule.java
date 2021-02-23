/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.core.premiums.configurations.impl;

import com.eisgroup.genesis.policy.core.premiums.api.rules.applicability.HolderApplicabilityContext;
import com.eisgroup.genesis.policy.core.premiums.api.rules.rounding.PremiumRoundingRule;
import java.math.RoundingMode;
import javax.annotation.Nonnull;

/**
 * Specific premium rounding rule for Bodily Injury coverage.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class BiCoveragePremiumRoundingRule implements PremiumRoundingRule {

    @Override
    public Integer prorateRoundingScale() {
        return 0;
    }

    @Override
    public RoundingMode prorateRoundingMode() {
        return RoundingMode.HALF_UP;
    }

    @Override
    public Integer factorRoundingScale() {
        return 4;
    }

    @Override
    public RoundingMode factorRoundingMode() {
        return RoundingMode.HALF_UP;
    }

    @Nonnull
    @Override
    public HolderApplicabilityContext applicabilityContext() {
        return HolderApplicabilityContext.of("BI");
    }
}