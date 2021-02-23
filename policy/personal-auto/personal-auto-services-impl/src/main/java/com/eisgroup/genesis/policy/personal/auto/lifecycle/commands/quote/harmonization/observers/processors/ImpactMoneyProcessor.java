/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.observers.processors;

import com.eisgroup.genesis.common.PrimitiveDataType;
import com.eisgroup.genesis.factory.model.types.modeled.ModeledAttribute;
import com.eisgroup.genesis.factory.model.types.modeled.ModeledAttributePrimitiveType;
import com.eisgroup.genesis.factory.model.types.modeled.ModeledEntity;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.aspects.ImpactDescriptor;
import com.google.gson.JsonObject;

import java.math.BigDecimal;

/**
 * Processes entities that match {@link PrimitiveDataType#MONEY} type
 *
 * @author sbelauski
 */
public class ImpactMoneyProcessor extends AbstractImpactProcessor {

    protected static final String AMOUNT = "amount";

    @Override
    public void applyValue(JsonObject object, ImpactDescriptor impactDescriptor) {
        final String impactAttribute = impactDescriptor.getEntityAttribute();
        final JsonObject moneyAsJsonObject = object.getAsJsonObject(impactAttribute);
        impactDescriptor.getResetValue().ifPresent(value -> moneyAsJsonObject.addProperty(AMOUNT, new BigDecimal(value)));
        impactDescriptor.isEmpty().ifPresent(isEmpty -> object.add(impactAttribute, null));
    }

    @Override
    protected boolean isApplicable(ModeledEntity model, ImpactDescriptor impactDescriptor) {
        final String impactAttribute = impactDescriptor.getEntityAttribute();
        final ModeledAttribute modeledAttribute = model.getAttributes().get(impactAttribute);

        if (modeledAttribute.getType() instanceof ModeledAttributePrimitiveType) {
            return PrimitiveDataType.MONEY.equals(((ModeledAttributePrimitiveType) modeledAttribute.getType()).getType());
        }

        return false;
    }
}
