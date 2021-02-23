/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.account;

import com.eisgroup.genesis.commands.request.CustomerAccountWriteRequest;
import com.eisgroup.genesis.exception.ErrorHolder;
import com.eisgroup.genesis.factory.model.customeraccount.GenesisCustomerAccount;
import com.eisgroup.genesis.factory.model.customeraccount.GenesisDesignatedContact;
import com.eisgroup.genesis.factory.model.lifecycle.Description;
import com.eisgroup.genesis.factory.model.lifecycle.Modifying;
import com.eisgroup.genesis.factory.modeling.types.CustomerAccount;
import com.eisgroup.genesis.factory.modeling.types.immutable.OrganizationalPerson;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;
import com.eisgroup.genesis.json.link.EntityLink;

import io.reactivex.Observable;

import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Valeriy Sizonenko
 */
@Modifying
@Description("cawh001: Creates new Customer account or updates existing.")
public class WriteHandlerRI extends com.eisgroup.genesis.commands.crm.account.WriteHandler {

    @Nonnull
    @Override
    public Observable<ErrorHolder> validateAsync(CustomerAccountWriteRequest request, CustomerAccount account) {
        GenesisCustomerAccount genesisCustomerAccount = ((GenesisCustomerAccount) request.getEntity());
        return super.validateAsync(request, account)
                .concatWith(validationService.checkAccessToAgency(genesisCustomerAccount.getAgency()))
                .concatWith(Observable.defer(() -> {
                    Collection<GenesisDesignatedContact> contacts = genesisCustomerAccount.getDesignatedContacts();
                    if (CollectionUtils.isNotEmpty(contacts)) {
                        List<EntityLink<? extends RootEntity>> agents = contacts.stream().map(GenesisDesignatedContact::getAgent).collect(Collectors.toList());
                        return validationService.checkEntityLinkType(agents, OrganizationalPerson.class, "designatedContacts[].agent");
                    }
                    return Observable.empty();                    
                }));
    }
}
