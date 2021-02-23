/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.commands.customer;

import com.eisgroup.genesis.command.Command;
import com.eisgroup.genesis.command.result.CommandResult;
import com.eisgroup.genesis.command.result.CommandSuccess;
import com.eisgroup.genesis.commands.crm.AbstractWriteHandler;
import com.eisgroup.genesis.commands.customer.request.CustomerSplitRequest;
import com.eisgroup.genesis.commands.saga.SagaCommandHandler;
import com.eisgroup.genesis.commands.saga.SubsequentStep;
import com.eisgroup.genesis.crm.commands.CrmCommands;
import com.eisgroup.genesis.crm.commands.request.CustomerPartialWriteRequest;
import com.eisgroup.genesis.exception.ErrorHolder;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.model.individualcustomer.IndividualCustomer;
import com.eisgroup.genesis.factory.model.organizationcustomer.OrganizationCustomer;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.eisgroup.genesis.json.link.EntityLink;
import com.eisgroup.genesis.model.Variation;
import com.eisgroup.genesis.registry.core.uniqueness.api.UniquenessCriteriaProvider;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * Abstract handler to split customer
 *
 * @author Valeriy Sizonenko
 * @since 10.4
 */

abstract public class SplitHandler extends AbstractWriteHandler<CustomerSplitRequest>
        implements SagaCommandHandler {

    protected static final String SPLITTED_FROM_ID = "splitFromId";

    @Autowired
    protected UniquenessCriteriaProvider uniquenessCriteriaProvider;

    @Nonnull
    @Override
    public Single<Customer> load(@Nonnull final CustomerSplitRequest request) {
        rewriteKey(request.getEntity().toJson());
        return super.load((request));
    }


    @Nonnull
    @Override
    public Observable<ErrorHolder> validateAsync(@Nonnull CustomerSplitRequest request, @Nonnull Customer emptyJson) {
        Customer customer = request.getEntity();
        return loadEntityByUUID(request.getSplitFromId())
                .toObservable()
                .map(prevJson -> ((Customer) ModelInstanceFactory.createInstance(prevJson)))
                .flatMap(prevCustomer -> customerValidator.checkIfPolicySummaryExistsForCustomer(prevCustomer)
                        .concatWith(customerValidator.compareCustomerUniqueFields(customer, prevCustomer, getUniqueFields(customer))))
                .concatWith(super.validateAsync(request, emptyJson));
    }

    @Nonnull
    @Override
    public Single<Customer> execute(@Nonnull CustomerSplitRequest request, @Nonnull Customer customer) {
        Customer curCust = request.getEntity();
        return loadEntityByUUID(request.getSplitFromId())
                .flatMap(prevJson -> {
                    Customer prevCustomer = (Customer) ModelInstanceFactory.createInstance(prevJson);
                    EntityLink link = builderRegistry.getByType(prevCustomer.getClass()).createLink(prevCustomer);
                    if (curCust instanceof IndividualCustomer) {
                        ((IndividualCustomer) curCust).setSplitFrom(link);
                    } else {
                        ((OrganizationCustomer) curCust).setSplitFrom(link);
                    }
                    return super.execute(request, customer);
                });
    }

    @Override
    public String getName() {
        return CrmCommands.SPLIT_CUSTOMER;
    }

    @Override
    public Observable<SubsequentStep> getSubsequentSteps(Command command, CommandResult commandResult) {
        Customer curCust = ((Customer) ModelInstanceFactory.createInstance(((CommandSuccess) commandResult).getData().getAsJsonObject()));
        return updatePrevCustomer(UUID.fromString(command.getData().getAsJsonObject().get(SPLITTED_FROM_ID).getAsString()), curCust)
                .toObservable()
                .map(prevCustomer -> new SubsequentStep(CrmCommands.WRITE_CUSTOMER, new CustomerPartialWriteRequest(prevCustomer), Variation.INVARIANT, modelResolver.getModelName()));
    }

    protected Collection<String> getUniqueFields(Customer customer) {
        return Collections.EMPTY_SET;
    }

    protected Single<Customer> updatePrevCustomer(UUID prevCustomerId, Customer curCustomer) {
        return Single.just(curCustomer);
    }
}