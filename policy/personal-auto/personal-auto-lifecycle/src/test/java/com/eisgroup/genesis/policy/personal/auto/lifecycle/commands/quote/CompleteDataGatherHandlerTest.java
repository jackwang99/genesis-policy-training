package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote;

import static org.mockito.Mockito.mock;

import java.util.UUID;

import com.eisgroup.genesis.commands.request.IdentifierRequest;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.PersonalAutoCommands;
import org.junit.Assert;
import org.junit.Test;

public class CompleteDataGatherHandlerTest {

    private CompleteDataGatherHandler handler = new CompleteDataGatherHandler();

    @Test
    public void shouldExecuteNothing() {
        PolicySummary policySummary = mock(PolicySummary.class);
        handler.execute(new IdentifierRequest(new RootEntityKey(UUID.randomUUID().toString(), 1)), policySummary)
                .test()
                .assertValue(context -> context.equals(policySummary));
    }

    @Test
    public void testGetName() {
        Assert.assertEquals(PersonalAutoCommands.COMPLETE_DATA_GATHER, handler.getName());

    }
}