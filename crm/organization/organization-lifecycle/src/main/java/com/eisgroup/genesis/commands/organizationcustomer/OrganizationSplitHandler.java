/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.commands.organizationcustomer;

import com.eisgroup.genesis.bam.CustomActivityTracking;
import com.eisgroup.genesis.commands.customer.SplitHandler;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.model.lifecycle.Description;
import com.eisgroup.genesis.factory.model.lifecycle.Modifying;
import com.eisgroup.genesis.factory.model.organizationcustomer.OrganizationCustomer;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.eisgroup.genesis.factory.repository.links.VersionRoot;
import com.eisgroup.genesis.json.link.EntityLink;
import com.eisgroup.genesis.json.link.LinkingParams;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import io.reactivex.Single;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static com.eisgroup.genesis.json.JsonEntity.TYPE_ATTRIBUTE;

/**
 * Handler to split organization customer
 *
 * @author Valeriy Sizonenko
 * @since 10.4
 */
@CustomActivityTracking
@Modifying
@Description("osh001: Splits organization Customer.")
public class OrganizationSplitHandler extends SplitHandler {

    @Override
    protected Collection<String> getUniqueFields(Customer curCustomer) {
        OrganizationCustomer orgCust = ((OrganizationCustomer) curCustomer);
        DomainModel domainModel = ModelRepositoryFactory.getRepositoryFor(DomainModel.class).getActiveModel(orgCust.getDetails().getLegalEntity().getModelFactory().getModelName());
        String type = domainModel.getTypes().get(orgCust.getDetails().getLegalEntity().toJson().get(TYPE_ATTRIBUTE).getAsString()).getBaseTypes().iterator().next();
        return uniquenessCriteriaProvider.getCriteria(type);
    }

    @Override
    protected Single<Customer> updatePrevCustomer(UUID prevCustomerId, Customer curCustomer) {
        return loadEntityByUUID(prevCustomerId)
                .map(prevJson -> {
                    OrganizationCustomer prevCustomer = (OrganizationCustomer) ModelInstanceFactory.createInstance(prevJson);
                    EntityLink link = builderRegistry.getByType(curCustomer.getClass()).createLink(curCustomer, LinkingParams.just(VersionRoot.class));
                    Collection splitToList = prevCustomer.getSplitTo();
                    if (splitToList == null) {
                        prevCustomer.setSplitTo(Arrays.asList(link));
                    } else {
                        Collections.addAll(splitToList, link);
                        prevCustomer.setSplitTo(splitToList);
                    }
                    return prevCustomer;
                });
    }
}