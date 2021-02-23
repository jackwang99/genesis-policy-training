/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.packaging.offer.listener;

import java.util.List;
import java.util.UUID;

import com.eisgroup.genesis.policy.core.routines.ModelTreeRoutine;
import org.springframework.beans.factory.annotation.Autowired;

import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.model.utils.DomainModelTree;
import com.eisgroup.genesis.factory.model.utils.TreeNode;
import com.eisgroup.genesis.factory.utils.selector.JsonNode;
import com.eisgroup.genesis.factory.utils.selector.ModelSelectUtil;
import com.eisgroup.genesis.json.key.EntityKey;
import com.eisgroup.genesis.model.ModelResolver;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author aspichakou
 *
 */
public class GfCoveragePusher {

    @Autowired
    private ModelResolver modelResolver;

    public void pushGfEntitites(JsonObject rootEntity, List<JsonNode> gfEntities) {

        final DomainModel model = modelResolver.resolveModel(DomainModel.class);

        final DomainModelTree domainModelTree = DomainModelTree.from(model);
        final JsonNode rootJsonNode = new JsonNode(domainModelTree.getRoot(), rootEntity, null);

        gfEntities.forEach(jsonNode -> {      
            final UUID parentId = new EntityKey(EntityKey.getKeyAttribute(jsonNode.getJson().getAsJsonObject())).getParentId().get();
            ModelSelectUtil.resolveNodeList(rootJsonNode, jsonNode.getParent().getSelectorNode())
                    .stream()
                    .filter(ModelTreeRoutine::isNotContainLinksInFullPath)
                    .filter(p -> p.getId().equals(parentId))
                    .forEach(p -> pushBack(p.getJson().getAsJsonObject(), jsonNode));
        });
    }

    private JsonObject pushBack(JsonObject object, JsonNode e) {
        final TreeNode node = e.getSelectorNode();

        final JsonElement jsonElement = object.get(node.getName());
        if (jsonElement != null) {
            if (jsonElement.isJsonArray()) {
                jsonElement.getAsJsonArray().add(e.getJson());
            } else {
                jsonElement.getAsJsonObject().add(node.getName(), e.getJson());
            }
        }
        else {
            boolean isArray = node.isArray();
            if (!isArray) {
                object.add(node.getName(), e.getJson());
            } else {
                final JsonArray jsonArray = new JsonArray();
                jsonArray.add(e.getJson());
                object.add(node.getName(), jsonArray);
            }
        }
        return object;
    }
}
