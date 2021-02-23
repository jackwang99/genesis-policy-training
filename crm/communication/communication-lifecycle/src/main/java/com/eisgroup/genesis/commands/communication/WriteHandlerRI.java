/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.commands.communication;


import com.eisgroup.genesis.bam.CustomActivityTracking;
import com.eisgroup.genesis.commands.crm.communication.WriteHandler;
import com.eisgroup.genesis.commands.request.CommunicationWriteRequest;
import com.eisgroup.genesis.exception.ErrorHolder;
import com.eisgroup.genesis.factory.model.communication.GenesisCommunication;
import com.eisgroup.genesis.factory.model.lifecycle.Description;
import com.eisgroup.genesis.factory.model.lifecycle.Modifying;
import com.eisgroup.genesis.factory.modeling.types.Communication;
import io.reactivex.Observable;


import javax.annotation.Nonnull;

/**
 * Provides functionality common across Communication command handlers.
 *
 * @author Valeriy Sizonenko
 * @module Communication
 * @since 10.8
 */
@CustomActivityTracking
@Modifying
@Description("opwh001: Creates new communication or updates existing.")
public class WriteHandlerRI extends WriteHandler {

    @Nonnull
    @Override
    public Observable<ErrorHolder> validateAsync(CommunicationWriteRequest request, Communication entity) {
        GenesisCommunication communication = ((GenesisCommunication) request.getEntity());
        return super.validateAsync(request, entity)
                .concatWith(validationService.checkAccessToAgency(communication.getAgency()));
    }
}