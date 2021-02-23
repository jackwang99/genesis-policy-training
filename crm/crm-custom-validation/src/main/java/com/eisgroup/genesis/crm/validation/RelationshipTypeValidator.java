/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.ApplicationContext;

import com.eisgroup.genesis.crm.validation.utils.ApplicationContextUtil;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.eisgroup.genesis.factory.modeling.types.CustomerRelationship;
import com.eisgroup.genesis.factory.modeling.types.IndividualCustomerBase;
import com.eisgroup.genesis.party.rdf.LegalEntityPersons;
import com.eisgroup.genesis.party.rdf.LegalEntityRelationships;
import com.eisgroup.genesis.party.rdf.PersonLegalEntities;
import com.eisgroup.genesis.party.rdf.PersonRelationships;
import com.eisgroup.genesis.rdf.Predicate;
import com.eisgroup.genesis.rdf.registry.PredicateRegistry;

/**
 * Relationship type validator for applicable values
 *
 * @author avoitau
 */
public class RelationshipTypeValidator implements ConstraintValidator
        <RelationshipType, Collection> {

    private Class<? extends Customer> type;

    @Override
    public void initialize(RelationshipType constraintAnnotation) {
        type = constraintAnnotation.type();
    }

    /**
     * Method to initialize validator. As a rule It's used to set init parameter.
     * This validator doesn't have any initialization and parameters.
     */
    @Override
    public boolean isValid(Collection relationships, ConstraintValidatorContext context) {
        if(CollectionUtils.isEmpty(relationships)) {
            return true;
        }

        return new ArrayList<CustomerRelationship>(relationships)
                .stream()
                .allMatch(relationship -> getRelationshipPredicates()
                .anyMatch(predicate -> predicate.getCode().equals(relationship.getRelationshipType())));
    }

    private Stream<Predicate> getRelationshipPredicates() {
        ApplicationContext springContext = ApplicationContextUtil.getApplicationContext();
        if(springContext != null) {
            PredicateRegistry predicateRegistry= springContext.getBean(PredicateRegistry.class);
            if(type.isAssignableFrom(IndividualCustomerBase.class)) {
                return Stream.concat(predicateRegistry.findByCode(PersonRelationships.GROUP_CD).stream(),
                        predicateRegistry.findByCode(PersonLegalEntities.GROUP_CD).stream());
            } else {
                return Stream.concat(predicateRegistry.findByCode(LegalEntityRelationships.GROUP_CD).stream(),
                        predicateRegistry.findByCode(LegalEntityPersons.GROUP_CD).stream());
            }
        }
        return Stream.empty();
    }
}
