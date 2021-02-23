/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.packaging.offer.listener;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.eisgroup.genesis.policy.core.routines.ModelTreeRoutine;
import org.springframework.beans.factory.annotation.Autowired;

import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.model.domain.relationship.Operand;
import com.eisgroup.genesis.factory.model.domain.relationship.SelectorElem;
import com.eisgroup.genesis.factory.model.utils.DomainModelTree;
import com.eisgroup.genesis.factory.modeling.types.GFCoverage;
import com.eisgroup.genesis.factory.utils.selector.JsonNode;
import com.eisgroup.genesis.factory.utils.selector.ModelSelectUtil;
import com.eisgroup.genesis.json.wrapper.JsonWrapperFactory;
import com.eisgroup.genesis.json.wrapper.JsonWrapperFactoryProvider;
import com.eisgroup.genesis.model.ModelResolver;
import com.google.gson.JsonObject;

/**
 * GrandFather Coverages collector
 * 
 * @author aspichakou
 *
 */
public class GfCoverageCollector {

    private JsonWrapperFactory wrapperFactory = JsonWrapperFactoryProvider.getJsonWrapperFactory();

    @Autowired
    private ModelResolver modelResolver;

    public List<JsonNode> getGfEntities(JsonObject rootEntity, LocalDate txEffective) {

        final DomainModel model = modelResolver.resolveModel(DomainModel.class);

        final DomainModelTree domainModelTree = DomainModelTree.from(model);
        final JsonNode rootJsonNode = new JsonNode(domainModelTree.getRoot(), rootEntity, null);

        final SelectorElem selectorElem = new SelectorElem(GFCoverage.NAME, null, null, Operand.DESCENDANT, null);
        final Collection<JsonNode> resolveBySelector = ModelSelectUtil.resolveBySelector(rootJsonNode, domainModelTree, selectorElem);

        return resolveBySelector.stream().filter(ModelTreeRoutine::isNotContainLinksInFullPath)
            .map(jsonNode -> {
                final GFCoverage gfCoverage =
                    wrapperFactory.wrap(jsonNode.getJson(), GFCoverage.class);

                if (gfCoverage.getExpirationDate() == null || gfCoverage.getEffectiveDate() == null
                    || txEffective.isBefore(gfCoverage.getEffectiveDate()) || !txEffective
                    .isBefore(gfCoverage.getExpirationDate())) {
                    return null;
                }

                return jsonNode;
            }).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
