/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.commands.customer.conversion;

import com.eisgroup.genesis.commands.crm.WriteHandler;
import com.eisgroup.genesis.crm.commands.ConversionCommands;
import com.eisgroup.genesis.crm.commands.request.CustomerPartialWriteRequest;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.model.lifecycle.Description;
import com.eisgroup.genesis.factory.modeling.types.AccessTrackInfo;
import com.eisgroup.genesis.factory.modeling.types.AccessTrackableEntity;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import io.reactivex.Single;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

/**
 * Command handler for customer/lead create/update actions (CRUD part) parses
 * JSON into write statements according to @link DomainModel
 *
 * @author Valeriy Sizonenko
 * @since 1.0
 */
@Description("cuwh001: Creates new Customer or updates existing.")
public class ConversionWriteHandler extends WriteHandler {

    @Override
    protected void copyLoadedCustomerFields(Customer loadedCustomer, JsonObject currentCustomerJson) {
        Customer currentCustomer = (Customer) ModelInstanceFactory.createInstance(currentCustomerJson);
        if(StringUtils.isBlank(currentCustomer.getCustomerNumber())) {
            currentCustomer.setCustomerNumber(loadedCustomer.getCustomerNumber());
        }
        copyAcessTrackInfo(loadedCustomer, currentCustomer);
    }

    public Single<Customer> execute(final CustomerPartialWriteRequest request, Customer customer) {
        return updateMajorAccount(request.getEntity());
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
    protected Collection<String> getSkippedEqualsFields() {
        return Sets.newHashSet("majorAccountId");
    }

    @Override
    public String getName() {
        return ConversionCommands.WRITE;
    }

    @Override
    protected void setupInitialDetails(Customer customer) {
        AccessTrackInfo newAccessTrackInfo = initAccessTrackInfo();
        if(customer.getAccessTrackInfo() == null) {
            customer.setAccessTrackInfo(newAccessTrackInfo);
        } else {
            setUnidentifiedFieldAccessTrackInfo(customer.getAccessTrackInfo(), newAccessTrackInfo);
        }
    }
}
