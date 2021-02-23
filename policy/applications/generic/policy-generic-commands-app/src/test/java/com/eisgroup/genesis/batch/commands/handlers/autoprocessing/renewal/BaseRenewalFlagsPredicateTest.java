/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.batch.commands.handlers.autoprocessing.renewal;

import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.model.testproduct.TestPolicySummary;
import com.eisgroup.genesis.test.utils.JsonUtils;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BaseRenewalFlagsPredicateTest {

    private BaseRenewalFlagsPredicate testObject = new BaseRenewalFlagsPredicate();
    private static final String POLICY_WITH_FLAGS_PATH = "testdata/quoteWithDoNotRenewFlag.json";
    private static final String POLICY_WITHOUT_FLAGS_PATH = "testdata/quoteWithoutRenewalRelatedFlags.json";
    private static final String POLICY_MODEL_NAME = "TestProduct";

    @Test
    public void shouldFilterPolicyWithFlags(){
        TestPolicySummary policyToTest  = (TestPolicySummary) ModelInstanceFactory.createInstance(
                POLICY_MODEL_NAME, "1", JsonUtils.load(POLICY_WITH_FLAGS_PATH));

        boolean result = testObject.test(policyToTest);

        assertFalse("Policy with doNotRenew, manualRenew or cancelNotice should not be picked",result);
    }

    @Test
    public void shouldNotFilterPolicyWithoutFlags(){
        TestPolicySummary policyToTest  = (TestPolicySummary) ModelInstanceFactory.createInstance(
                POLICY_MODEL_NAME, "1", JsonUtils.load(POLICY_WITHOUT_FLAGS_PATH));

        boolean result = testObject.test(policyToTest);

        assertTrue("Policy without doNotRenew, manualRenew and cancelNotice should be picked",result);
    }

}
