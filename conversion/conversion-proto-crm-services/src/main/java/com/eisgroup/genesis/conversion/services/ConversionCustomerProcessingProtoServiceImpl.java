/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.conversion.services;

import java.util.Optional;

import javax.annotation.Nonnull;

import com.eisgroup.genesis.conversion.ConversionEntityContext;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.eisgroup.genesis.factory.modeling.types.CustomerSplitInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;
import com.eisgroup.genesis.json.link.EntityLink;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Implementation of
 * {@link com.eisgroup.genesis.conversion.services.ConversionModelEntityProcessingService}
 * 
 * @author andrisf
 * @since July 02, 2019
 */
public class ConversionCustomerProcessingProtoServiceImpl extends ConversionCustomerProcessingServiceImpl {

    /**
     * Prepares the conversion entity
     * 
     * @param conversionEntity
     * @param existingEntity
     * @param processEntityContext
     * 
     * @return the model root entity
     */
    @Override
    @Nonnull
    protected Single<RootEntity> prepareConversionEntity(@Nonnull RootEntity conversionEntity,
            @Nonnull Optional<RootEntity> existingEntity, @Nonnull ConversionEntityContext processEntityContext) {

        return super.prepareConversionEntity(conversionEntity, existingEntity, processEntityContext)
                .flatMap(preparedEntity -> {
                    Customer customer = (Customer) preparedEntity;

                    return processCustomerSplitFrom(customer, processEntityContext)
                            .andThen(processCustomerSplitTo(customer, processEntityContext)).toSingleDefault(customer);
                });
    }

    /**
     * Method for setting the customer split to
     * 
     * @param customer
     * @param processEntityContext
     * @return a Completable instance
     */
    @Nonnull
    protected Completable processCustomerSplitTo(@Nonnull Customer customer,
            @Nonnull ConversionEntityContext processEntityContext) {

        return Completable.defer(() -> {
            if (!(customer instanceof CustomerSplitInfo)) {
                return Completable.complete();
            }
            CustomerSplitInfo customerSplitInfo = (CustomerSplitInfo) customer;
            return processModelEntityLinks(customerSplitInfo.getSplitTo(), processEntityContext).toList()
                    .flatMapCompletable(
                            entityLinks -> Completable.fromAction(() -> customerSplitInfo.setSplitTo(entityLinks)));
        });
    }

    /**
     * Method for setting the customer split from
     * 
     * @param customer
     * @param processEntityContext
     * @return a Completable instance
     */
    @Nonnull
    protected Completable processCustomerSplitFrom(@Nonnull Customer customer,
            @Nonnull ConversionEntityContext processEntityContext) {

        return Completable.defer(() -> {

            if (!(customer instanceof CustomerSplitInfo)) {
                return Completable.complete();
            }
            CustomerSplitInfo customerSplitInfo = (CustomerSplitInfo) customer;
            EntityLink<com.eisgroup.genesis.factory.modeling.types.immutable.Customer> splitFrom = customerSplitInfo
                    .getSplitFrom();

            if (splitFrom == null) {
                return Completable.complete();
            }
            return processModelEntityLink(splitFrom, processEntityContext).flatMapCompletable(
                    entityLink -> Completable.fromAction(() -> customerSplitInfo.setSplitFrom(entityLink)));
        });
    }
}
