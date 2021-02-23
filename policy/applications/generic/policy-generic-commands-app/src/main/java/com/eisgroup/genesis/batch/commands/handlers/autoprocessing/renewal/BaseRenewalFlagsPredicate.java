/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.batch.commands.handlers.autoprocessing.renewal;

import com.eisgroup.genesis.factory.modeling.types.PolicySummary;

import java.util.function.Predicate;

import static org.apache.commons.lang3.BooleanUtils.isNotTrue;

public class BaseRenewalFlagsPredicate implements Predicate<PolicySummary> {

    /**
     * Policies marked with Do not Renew or Manual should not be picked for the renewal process
     * Policies that already have renewal quote should not be picked for the renewal process
     *
     * @param policySummary policy to filter
     * @return Should this policy be peaked up by this job or not
     */
    @Override
    public boolean test(PolicySummary policySummary) {
        return isNotTrue(policySummary.getPolicyDetail().getDoNotRenew())
                && isNotTrue(policySummary.getPolicyDetail().getManualRenew())
                && isNotTrue(policySummary.getPolicyDetail().getCancelNotice());
    }
}
