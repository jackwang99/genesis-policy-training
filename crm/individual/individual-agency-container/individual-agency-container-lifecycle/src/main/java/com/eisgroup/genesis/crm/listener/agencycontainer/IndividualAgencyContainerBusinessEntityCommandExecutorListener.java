/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.listener.agencycontainer;

import com.eisgroup.genesis.commands.publisher.api.CommandPublisher;
import com.eisgroup.genesis.commands.request.AbstractApplyRequest;
import com.eisgroup.genesis.crm.listener.container.AbstractAgencyContainerBusinessEntityCommandExecutorListener;

/**
 * 
 * @author Dmitry Andronchik
 * @since 9.13
 */
public class IndividualAgencyContainerBusinessEntityCommandExecutorListener extends AbstractAgencyContainerBusinessEntityCommandExecutorListener<AbstractApplyRequest> {

    public IndividualAgencyContainerBusinessEntityCommandExecutorListener(CommandPublisher commandPublisher) {
        super(commandPublisher);
    }

    @Override
    protected String getCustomerModelName() {
        return "INDIVIDUALCUSTOMER";
    }
}
