/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.conversion.services;

import java.util.Optional;

import javax.annotation.Nonnull;

import com.eisgroup.genesis.conversion.ConversionEntityContext;
import com.eisgroup.genesis.factory.model.individualcustomer.IndividualCustomer;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Implementation of
 * {@link com.eisgroup.genesis.conversion.services.ConversionModelEntityProcessingService}
 * 
 * @author achobotar
 * @since July 17, 2019
 */
public class ConversionIndividualCustomerProcessingProtoServiceImpl
        extends ConversionCustomerProcessingProtoServiceImpl {

    private ConversionCustomerRelationshipResolverService conversionCustomerRelationshipResolverService;

    /**
     * Prepares the conversion entity
     *
     * @param conversionEntity
     * @param existingEntity
     * @param processEntityContext
     *
     * @return the model root entity
     */
    @Nonnull
    @Override
    protected Single<RootEntity> prepareConversionEntity(@Nonnull RootEntity conversionEntity,
            @Nonnull Optional<RootEntity> existingEntity, @Nonnull ConversionEntityContext processEntityContext) {

        return super.prepareConversionEntity(conversionEntity, existingEntity, processEntityContext)
                .flatMap(preparedEntity -> {
                    Customer customer = (Customer) preparedEntity;

                    return processParticipationInfoImpl(customer, processEntityContext).toSingleDefault(customer);
                });
    }

    /**
     * Resolves links in all participation details instances
     *
     * @param customer
     * @param processEntityContext
     * @return a Completable instance
     */
    @Nonnull
    protected Completable processParticipationInfoImpl(@Nonnull Customer customer,
            @Nonnull ConversionEntityContext processEntityContext) {

        return Completable.defer(() -> {
            IndividualCustomer individualCustomer = (IndividualCustomer) customer;

            if (individualCustomer.getParticipationInfo() == null) {
                return Completable.complete();
            }
            return conversionCustomerRelationshipResolverService
                    .processParticipationDetail(individualCustomer.getParticipationInfo().getEmployments(),
                            processEntityContext)
                    .andThen(conversionCustomerRelationshipResolverService.processParticipationDetail(
                            individualCustomer.getParticipationInfo().getMemberships(), processEntityContext))
                    .andThen(conversionCustomerRelationshipResolverService.processParticipationDetail(
                            individualCustomer.getParticipationInfo().getStudents(), processEntityContext));
        });
    }

    public void setConversionCustomerRelationshipResolverService(
            ConversionCustomerRelationshipResolverService conversionCustomerRelationshipResolverService) {
        this.conversionCustomerRelationshipResolverService = conversionCustomerRelationshipResolverService;
    }

    protected ConversionCustomerRelationshipResolverService getConversionCustomerRelationshipResolverService() {
        return conversionCustomerRelationshipResolverService;
    }
    
    
}
