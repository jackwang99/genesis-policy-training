/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.commands.opportunity.conversion;

import com.eisgroup.genesis.bam.CustomActivityTracking;
import com.eisgroup.genesis.commands.opportunity.WriteHandlerRI;
import com.eisgroup.genesis.crm.commands.ConversionCommands;
import com.eisgroup.genesis.factory.model.lifecycle.Description;
import com.eisgroup.genesis.factory.model.lifecycle.Modifying;
import com.eisgroup.genesis.factory.modeling.types.AccessTrackInfo;
import com.eisgroup.genesis.factory.modeling.types.AccessTrackableEntity;
import com.eisgroup.genesis.factory.modeling.types.Opportunity;
import com.google.common.collect.Sets;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

/**
 * Command handler for Conversion team
 * Create/update Opportunity without some validations
 *
 * @author Valeriy Sizonenko
 * @since 10.1
 */
@CustomActivityTracking
@Modifying
@Description("opwh001: Creates new opportunity or updates existing.")
public class ConversionWriteHandler extends WriteHandlerRI {

    @Override
    public String getName() {
        return ConversionCommands.WRITE;
    }
    
    @Override
    protected void copyLoadedEntityFields(Opportunity loaded, Opportunity current) {
        super.copyLoadedEntityFields(loaded, current);
        
        if(StringUtils.isBlank(current.getOpportunityId())) {
            current.setOpportunityId(loaded.getOpportunityId());
        }
    }     

    @Override
    protected void initAccessTrackInfo(AccessTrackableEntity entity) {
        AccessTrackInfo newAccessTrackInfo = initAccessTrackInfo();
        if(entity.getAccessTrackInfo() == null) {
            entity.setAccessTrackInfo(newAccessTrackInfo);
        } else {
            setUnidentifiedFieldAccessTrackInfo(entity.getAccessTrackInfo(), newAccessTrackInfo);
        }
    }

    @Override
    protected void copyAcessTrackInfo(AccessTrackableEntity loaded, AccessTrackableEntity current) {
        if (current.getAccessTrackInfo() == null) {
            if (loaded.getAccessTrackInfo() == null) {
                super.initAccessTrackInfo(current);
            } else {
                current.setAccessTrackInfo(loaded.getAccessTrackInfo());
            }
        } else {
            AccessTrackInfo loadedAccessTrackInfo = (loaded.getAccessTrackInfo() == null) ? initAccessTrackInfo() : loaded.getAccessTrackInfo();
            setUnidentifiedFieldAccessTrackInfo(current.getAccessTrackInfo(), loadedAccessTrackInfo);
        }
    }

    private void setUnidentifiedFieldAccessTrackInfo(AccessTrackInfo current, AccessTrackInfo loaded){
        if (current.getCreatedOn() == null){
            current.setCreatedOn(loaded.getCreatedOn());
        }
        if (current.getUpdatedOn() == null){
            current.setUpdatedOn(loaded.getUpdatedOn());
        }
        if (current.getCreatedBy() == null){
            current.setCreatedBy(loaded.getCreatedBy());
        }
        if (current.getUpdatedBy() == null){
            current.setUpdatedBy(loaded.getUpdatedBy());
        }
    }

    @Override
    protected void setupInitialDetails(Opportunity opportunity) {
        initAccessTrackInfo(opportunity);
    }
    
    @Override
    protected void updateAccessTrackInfo(AccessTrackableEntity entity) {
        
    }

    @Override
    protected Collection<String> getSkippedEqualsFields() {
        return Sets.newHashSet();
    }
}
