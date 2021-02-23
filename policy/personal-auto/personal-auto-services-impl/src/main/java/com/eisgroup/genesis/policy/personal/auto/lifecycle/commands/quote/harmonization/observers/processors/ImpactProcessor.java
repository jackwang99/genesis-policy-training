/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.observers.processors;

import com.eisgroup.genesis.factory.model.types.modeled.ModeledEntity;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.aspects.ImpactDescriptor;
import com.google.gson.JsonObject;

/**
 * Processes impacted entities
 *
 * @author sbelauski
 */
public interface ImpactProcessor {

    void process(ModeledEntity model, JsonObject object, ImpactDescriptor impactDescriptor);
}
