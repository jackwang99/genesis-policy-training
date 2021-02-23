/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.packaging.applicability;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.eisgroup.genesis.exception.ErrorHolder;
import com.eisgroup.genesis.factory.model.utils.TreeNode;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.factory.modeling.types.TransactionDetails;
import com.eisgroup.genesis.factory.utils.selector.JsonNode;
import com.eisgroup.genesis.json.JsonEntity;
import com.eisgroup.genesis.json.key.EntityKey;
import com.eisgroup.genesis.json.wrapper.JsonWrapperFactory;
import com.eisgroup.genesis.json.wrapper.JsonWrapperFactoryProvider;
import com.eisgroup.genesis.packaging.PackagingGlobalDelta;
import com.eisgroup.genesis.packaging.PackagingWorkingContext;
import com.eisgroup.genesis.packaging.context.PackageContext;
import com.eisgroup.genesis.packaging.offer.listener.GfCoverageCollector;
import com.google.gson.JsonObject;

/**
 * Grandfathering-respecting validation service
 * 
 * @author aspichakou
 * @since 1.0
 */
public class PersonalAutoComponentApplicabilityValidateServiceImpl<T extends PackageContext> extends ComponentApplicabilityValidateServiceImpl<T> {

    private JsonWrapperFactory wrapperFactory = JsonWrapperFactoryProvider.getJsonWrapperFactory();
    
    @Autowired
    private GfCoverageCollector gfCollector;
  
    @Override
    protected List<ErrorHolder> prepareError(JsonEntity root, PackageContext context, PackagingGlobalDelta globalDelta) {
        final String packageName = (String) context.getPackageDimensions().get("packageCd");

        final PolicySummary policy = wrapperFactory.wrap(root.toJson(), PolicySummary.class);
        final TransactionDetails transactionDetails = policy.getTransactionDetails();
        final LocalDate txEffective = transactionDetails.getTxEffectiveDate().toLocalDate();
        
        final List<JsonNode> gfEntities = gfCollector.getGfEntities(root.toJson(), txEffective);
        final List<UUID> bypass = gfEntities.stream().map(j -> new EntityKey(EntityKey.getKeyAttribute(j.getJson().getAsJsonObject())).getId()).collect(Collectors.toList());

        final Map<String, Set<TreeNode>> processed = new HashMap<>();
        final List<ErrorHolder> errors = new ArrayList<>();
        globalDelta.getGlobalDelta().forEach(d -> {
            d.getDeltaAdd().values().forEach(entity -> appendError(errors, processed, d, entity, true, packageName));
            d.getDeltaRemove().values().forEach(entity -> {
                final String entityName = entity.getEntityName();
                final PackagingWorkingContext relationshipWorkingContext = d.getWorkingContext().get(entityName);
                final JsonObject keyAttribute = EntityKey.getKeyAttribute(relationshipWorkingContext.getChildNode().getJson().getAsJsonObject());
                final EntityKey entityKey = new EntityKey(keyAttribute);

                final UUID id = entityKey.getId();
                if (!bypass.contains(id)) {
                    appendError(errors, processed, d, entity, false, packageName);
                }
            });

        });

        return errors;
    }
}
