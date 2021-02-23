/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote;

import com.eisgroup.genesis.commands.request.IdentifierRequest;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.PersonalAutoCommands;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

/**
 * Unit tests for {@link  CompleteOfferHandler}
 *
 * @author aprudnikau
 * @since 10.5
 */
@RunWith(MockitoJUnitRunner.class)
public class CompleteOfferHandlerTest {

    @InjectMocks
    private CompleteOfferHandler handler;

    @Mock
    private PolicySummary context;

    @Test
    public void testGetName() {
        Assert.assertEquals(PersonalAutoCommands.COMPLETE_OFFER, handler.getName());
    }

    @Test
    public void testExecute() {
        handler.execute(new IdentifierRequest(new RootEntityKey(UUID.randomUUID().toString(), 1)), context)
                .test()
                .assertValue(executedContext -> executedContext.equals(context));
    }
}
