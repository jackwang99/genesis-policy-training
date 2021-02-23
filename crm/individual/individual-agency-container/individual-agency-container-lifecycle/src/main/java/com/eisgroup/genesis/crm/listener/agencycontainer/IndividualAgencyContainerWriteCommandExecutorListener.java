/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.listener.agencycontainer;

import com.eisgroup.genesis.commands.publisher.api.CommandPublisher;
import com.eisgroup.genesis.commands.request.IndividualAgencyContainerWriteRequest;
import com.eisgroup.genesis.crm.listener.container.AbstractAgencyContainerWriteCommandExecutorListener;
import com.eisgroup.genesis.factory.modeling.types.IndividualAgencyContainer;
import com.eisgroup.genesis.factory.modeling.types.immutable.IndividualCustomerBase;
import com.eisgroup.genesis.json.link.EntityLink;
import com.eisgroup.genesis.json.link.EntityLinkResolverRegistry;

/**
 * @author Dmitry Andronchik
 */
public class IndividualAgencyContainerWriteCommandExecutorListener extends AbstractAgencyContainerWriteCommandExecutorListener<IndividualAgencyContainerWriteRequest, IndividualAgencyContainer, IndividualCustomerBase> {
    
    public IndividualAgencyContainerWriteCommandExecutorListener(CommandPublisher commandPublisher, EntityLinkResolverRegistry linkResolverRegistry) {
        super(commandPublisher, linkResolverRegistry);
    }

    @Override
    protected EntityLink<IndividualCustomerBase> resolveCustomerEntityLink(IndividualAgencyContainer agencyContainer) {
        return agencyContainer.getCustomer();
    }

    @Override
    protected String getCustomerModelName() {
        return "INDIVIDUALCUSTOMER";
    }
}
