/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.crm.services.opportunity;

import com.eisgroup.genesis.commands.services.opportunity.OpportunityValidationService;
import com.eisgroup.genesis.crm.validation.Paths;
import com.eisgroup.genesis.exception.ErrorHolder;
import com.eisgroup.genesis.factory.model.opportunity.GenesisUserOwner;
import com.eisgroup.genesis.factory.modeling.types.OrganizationalPerson;
import com.eisgroup.genesis.factory.modeling.types.Owner;
import io.reactivex.Observable;

/**
 * Reference implementation of Opportunity validation service
 *
 * @author Valeriy Sizonenko
 * @since 10.10
 */
public class OpportunityValidationServiceImpl extends OpportunityValidationService {

    public Observable<ErrorHolder> validateOwner(Owner owner) {
        if (owner instanceof GenesisUserOwner) {
            GenesisUserOwner userOwner = ((GenesisUserOwner) owner);
            return crmValidationService.checkEntityLinkType(userOwner.getLink(), OrganizationalPerson.class, Paths.buildPath("owner", Paths.LINK));
        }
        return Observable.empty();
    }
}