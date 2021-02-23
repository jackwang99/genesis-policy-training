/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.events.listener;

import com.eisgroup.genesis.command.result.CommandFailure;
import com.eisgroup.genesis.command.result.CommandResult;
import com.eisgroup.genesis.crm.commands.CrmCommands;
import com.eisgroup.genesis.crm.commands.request.CustomerPartialWriteRequest;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.modeling.types.CrmCommunicationInfo;
import com.eisgroup.genesis.factory.modeling.types.CrmEmail;
import com.eisgroup.genesis.factory.modeling.types.CrmPhone;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.eisgroup.genesis.factory.modeling.types.immutable.CommunicationInfo;
import com.eisgroup.genesis.model.external.ExternalModelRepository;
import com.google.gson.JsonObject;
import io.reactivex.Single;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author Dmitry Andronchik
 * @since 9.13
 */
public abstract class BaseOrgStuctureWriteCrmHandler extends BaseExternalEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(BaseOrgStuctureWriteCrmHandler.class);

    protected final String customerModelName;
    protected final String customerModelVersion;
    protected final CustomerCommandEventHandler customerCommandEventHandler;

    public BaseOrgStuctureWriteCrmHandler(String customerModelName, String customerModelVersion,
                                          Predicate<String> filterFunction, ExternalModelRepository externalModelRepo, CustomerCommandEventHandler customerCommandEventHandler) {
        super(filterFunction, externalModelRepo);
        this.customerModelName = customerModelName;
        this.customerModelVersion = customerModelVersion;
        this.customerCommandEventHandler = customerCommandEventHandler;
    }

    protected void sendCustomerWriteCommand(Customer customer) {
        CommandResult commandResult = customerCommandEventHandler.executeCommand(CrmCommands.WRITE_CUSTOMER, customerModelName,
                new CustomerPartialWriteRequest(customer).toJson()).blockingGet();
        if (commandResult.isFailure()) {
            logger.warn("Can't write customer: " + ((CommandFailure) commandResult).getData());
        }
    }

    protected CrmCommunicationInfo initCommunicationInfo(CommunicationInfo orgCommunicationInfo) {

        CrmCommunicationInfo communicationInfo = (CrmCommunicationInfo) ModelInstanceFactory
                .createInstanceByBusinessType(customerModelName, customerModelVersion, CrmCommunicationInfo.class.getSimpleName());

        if (orgCommunicationInfo != null) {
            if (!CollectionUtils.isEmpty(orgCommunicationInfo.getEmails())) {
                communicationInfo.setEmails(orgCommunicationInfo.getEmails().stream().map(orgEmail -> {
                    CrmEmail crmEmail = (CrmEmail) ModelInstanceFactory.createInstanceByBusinessType(customerModelName,
                            customerModelVersion, CrmEmail.class.getSimpleName());
                    crmEmail.setType(orgEmail.getType());
                    crmEmail.setValue(orgEmail.getValue());
                    return crmEmail;
                }).collect(Collectors.toList()));
            }
            if (!CollectionUtils.isEmpty(orgCommunicationInfo.getPhones())) {
                communicationInfo.setPhones(orgCommunicationInfo.getPhones().stream().map(orgPhone -> {
                    CrmPhone crmPhone = (CrmPhone) ModelInstanceFactory.createInstanceByBusinessType(customerModelName,
                            customerModelVersion, CrmPhone.class.getSimpleName());
                    crmPhone.setType(orgPhone.getType());
                    crmPhone.setValue(orgPhone.getValue());
                    crmPhone.setPhoneExtension(orgPhone.getPhoneExtension());
                    return crmPhone;
                }).collect(Collectors.toList()));
            }
        }
        return communicationInfo;
    }

    public Single<CommandResult> initNewCustomer() {
        return customerCommandEventHandler.executeCommand(CrmCommands.INIT_CUSTOMER, customerModelName,
                new JsonObject());
    }
}
