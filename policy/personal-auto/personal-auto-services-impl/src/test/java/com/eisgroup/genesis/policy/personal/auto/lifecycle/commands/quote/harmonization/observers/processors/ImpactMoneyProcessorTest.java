package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.observers.processors;

import com.eisgroup.genesis.common.PrimitiveDataType;
import com.eisgroup.genesis.factory.model.types.modeled.ModeledAttribute;
import com.eisgroup.genesis.factory.model.types.modeled.ModeledAttributePrimitiveType;
import com.eisgroup.genesis.factory.model.types.modeled.ModeledEntity;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.aspects.ImpactDescriptor;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ImpactMoneyProcessorTest {

    private ImpactMoneyProcessor impactMoneyProcessor = new ImpactMoneyProcessor();

    private static final String IMPACTED_ATTRIBUTE = "deductibleLimit";
    private static final int INITIAL_AMOUNT = 20;
    private static final int RESETTED_AMOUNT = 100;
    private static final Object EMPTY_AMOUNT = JsonNull.INSTANCE;

    private ModeledEntity modeledEntity;
    private JsonObject target = new JsonObject();
    private JsonObject money = new JsonObject();

    @Mock
    private ImpactDescriptor impactDescriptor;

    @Before
    public void setUpTest() {
        final PrimitiveDataType primitiveDataType = PrimitiveDataType.MONEY;
        final ModeledAttribute modeledAttribute = new ModeledAttribute(null, new ModeledAttributePrimitiveType(primitiveDataType), null, null);
        modeledEntity = new ModeledEntity(null, Collections.singletonMap(IMPACTED_ATTRIBUTE, modeledAttribute), null, null, null, null, null);

        money.addProperty(ImpactMoneyProcessor.AMOUNT, INITIAL_AMOUNT);
        target.add(IMPACTED_ATTRIBUTE, money);

        when(impactDescriptor.getEntityAttribute()).thenReturn(IMPACTED_ATTRIBUTE);
    }

    @Test
    public void shouldResetValue() {
        when(impactDescriptor.getResetValue()).thenReturn(Optional.of(Integer.toString(RESETTED_AMOUNT)));
        impactMoneyProcessor.process(modeledEntity, target, impactDescriptor);
        assertThat(money.get(ImpactMoneyProcessor.AMOUNT).getAsInt(), is(RESETTED_AMOUNT));
    }

    @Test
    public void shouldClearValue() {
        when(impactDescriptor.isEmpty()).thenReturn(Optional.of(true));
        impactMoneyProcessor.process(modeledEntity, target, impactDescriptor);
        assertEquals(JsonNull.INSTANCE, target.get(impactDescriptor.getEntityAttribute()));
    }

    @Test
    public void shouldRemainValue() {
        impactMoneyProcessor.process(modeledEntity, target, impactDescriptor);
        assertThat(money.get(ImpactMoneyProcessor.AMOUNT).getAsInt(), is(INITIAL_AMOUNT));
    }
}