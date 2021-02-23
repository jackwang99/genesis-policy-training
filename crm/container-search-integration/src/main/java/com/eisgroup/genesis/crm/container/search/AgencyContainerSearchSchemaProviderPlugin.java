/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.eisgroup.genesis.crm.container.search;

import com.eisgroup.genesis.crm.core.service.search.BaseModeledEntitySearchSchemaProviderPlugin;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.model.utils.DomainModelTree;
import com.eisgroup.genesis.search.schema.SearchField;
import com.google.common.collect.Sets;

import io.reactivex.Observable;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * Creates additional fields for {@Customer} from targetModel.
 *
 * @author Dmitry Andronchik
 * @since 10.8
 */
public class AgencyContainerSearchSchemaProviderPlugin extends BaseModeledEntitySearchSchemaProviderPlugin {
    
    private final String agencyModelName;
    private final String customerModelName;

    public AgencyContainerSearchSchemaProviderPlugin(String customerModelName, String agencyModelName) {
        this.customerModelName = customerModelName;
        this.agencyModelName = agencyModelName;
    }

    @Override
    public Collection<SearchField> resolveAdditionalFields(DomainModel model) {
        if (!StringUtils.equals(model.getName(), customerModelName)) {
            return Sets.newHashSet();
        }
        return getSearchableModelFields(agencyModelName);
    }
    
    @Override
    protected Observable<SearchField> getAdditionalModelFields(DomainModelTree domainModelTree, String modelName) {
        return getDimensionFields(domainModelTree, modelName).flatMap(node -> toFields(node, true));
    }    
}