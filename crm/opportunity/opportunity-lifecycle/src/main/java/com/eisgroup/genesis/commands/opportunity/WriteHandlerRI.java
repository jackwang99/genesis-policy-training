/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.commands.opportunity;

import com.eisgroup.genesis.bam.CustomActivityTracking;
import com.eisgroup.genesis.commands.crm.opportunity.WriteHandler;
import com.eisgroup.genesis.crm.commands.request.OpportunityWriteRequest;
import com.eisgroup.genesis.exception.ErrorHolder;
import com.eisgroup.genesis.factory.model.lifecycle.Description;
import com.eisgroup.genesis.factory.model.lifecycle.Modifying;
import com.eisgroup.genesis.factory.model.opportunity.GenesisOpportunity;
import com.eisgroup.genesis.factory.modeling.types.Opportunity;
import io.reactivex.Observable;

import javax.annotation.Nonnull;

/**
 * Provides functionality common across Opportunity command handlers.
 *
 * @author Valeriy Sizonenko
 * @module Opportunity
 * @since 10.1
 */
@CustomActivityTracking
@Modifying
@Description("opwh001: Creates new opportunity or updates existing.")
public class WriteHandlerRI extends WriteHandler {

    @Nonnull
    @Override
    public Observable<ErrorHolder> validateAsync(OpportunityWriteRequest request, Opportunity entity) {
        GenesisOpportunity opportunity = ((GenesisOpportunity) request.getEntity());
        return super.validateAsync(request, entity)
                .concatWith(validationService.checkAccessToAgency(opportunity.getAgency()))
                .concatWith(opportunityValidationService.validateCampaignId(opportunity.getCampaignId()))
                .concatWith(opportunityValidationService.validateOwner(opportunity.getOwner()));
    }
}