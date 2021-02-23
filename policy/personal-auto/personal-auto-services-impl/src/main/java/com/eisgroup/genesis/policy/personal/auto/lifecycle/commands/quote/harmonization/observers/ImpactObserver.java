/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.observers;

import com.eisgroup.genesis.factory.json.IdentifiableEntity;
import com.eisgroup.genesis.factory.model.types.modeled.ModeledEntity;
import com.eisgroup.genesis.factory.utils.ModelNode;
import com.eisgroup.genesis.factory.utils.TraverseObserver;
import com.eisgroup.genesis.json.wrapper.JsonWrapperFactory;
import com.eisgroup.genesis.json.wrapper.JsonWrapperFactoryProvider;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.aspects.ImpactDescriptor;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.observers.processors.ImpactProcessor;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * Modifies entity node value by applying {@link ImpactProcessor} from correspondent list
 *
 * @author sbelauski
 */
public class ImpactObserver implements TraverseObserver {

    private final List<ImpactDescriptor> impactDescriptors;
    private final List<ImpactProcessor> impactProcessors;
    private final JsonWrapperFactory jsonWrapperFactory = JsonWrapperFactoryProvider.getJsonWrapperFactory();

    public ImpactObserver(List<ImpactDescriptor> impactDescriptors, List<ImpactProcessor> impactProcessors) {
        this.impactDescriptors = impactDescriptors;
        this.impactProcessors = impactProcessors;
    }

    @Override
    public TraverseObserver onEntityNode(JsonObject object, String type, ModelNode.NodePointer nodePointer, ModeledEntity model) {
        impactDescriptors.stream()
                .filter(impactDescriptor -> model.isAssignableTo(impactDescriptor.getEntityName()))
                .filter(impactDescriptor -> matchesPath(object, impactDescriptor))
                .forEach(impactDescriptor ->
                        impactProcessors.forEach(impactProcessor ->
                                impactProcessor.process(model, object, impactDescriptor)));

        return this;
    }

    private boolean matchesPath(JsonObject object, ImpactDescriptor impactDescriptor) {
        return impactDescriptor.getPath().contains(
                jsonWrapperFactory.wrap(object, IdentifiableEntity.class)
                        .getKey()
                        .getParentId()
                        .map(UUID::toString)
                        .orElse(StringUtils.EMPTY));
    }
}
