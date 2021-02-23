/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.validation;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.eisgroup.genesis.factory.modeling.types.immutable.CrmDimensionValueHolder;


/**
 * 
 * @author Dmitry Andronchik
 */
public class UniqueDimensionAgencyValidator implements ConstraintValidator<UniqueDimensionAgency, Collection<? extends CrmDimensionValueHolder>> {

    @Override
    public void initialize(UniqueDimensionAgency constraintAnnotation) {
    }

    @Override
    public boolean isValid(Collection<? extends CrmDimensionValueHolder> dimensionHolders, ConstraintValidatorContext context) {
        
        if(CollectionUtils.isEmpty(dimensionHolders)) {
            return true;
        }

        long duplicatesNumber = dimensionHolders.stream()
                                  .filter(dh -> dh != null && !StringUtils.isBlank(dh.getAgency()))
                                  .collect(Collectors.groupingBy(CrmDimensionValueHolder::getAgency, Collectors.counting()))
                                  .values()
                                  .stream()
                                  .filter(count -> count > 1)
                                  .count();
                  
        return duplicatesNumber == 0;
    }
}
