/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.validation;

import java.time.LocalDate;

import org.hibernate.validator.internal.util.annotationfactory.AnnotationDescriptor;
import org.hibernate.validator.internal.util.annotationfactory.AnnotationFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Dmitry Andronchik
 * @since 10.3
 */
public class BirthdayRangeValidatorTest {

    private BirthdayRangeValidator validator = new BirthdayRangeValidator();

    @Test(expected=IllegalArgumentException.class)
    public void initializeMinBelowZero() {
     
        BirthdayRange parameters = createAnnotation(-1, 5);
        validator.initialize(parameters);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void initializeMaxBelowZero() {
     
        BirthdayRange parameters = createAnnotation(3, -5);
        validator.initialize(parameters);
    }    
    
    @Test(expected=IllegalArgumentException.class)
    public void initializeMaxBelowMin() {
     
        BirthdayRange parameters = createAnnotation(3, 1);
        validator.initialize(parameters);
    }
    
    @Test
    public void testValidBirthday() {
        
        BirthdayRange parameters = createAnnotation(18, 50);
        validator.initialize(parameters);
        
        Assert.assertTrue(validator.isValid(LocalDate.now().minusYears(20), null));
    }
    
    @Test
    public void testBirthdayLessMin() {
        
        BirthdayRange parameters = createAnnotation(18, 50);
        validator.initialize(parameters);
        
        Assert.assertFalse(validator.isValid(LocalDate.now().minusYears(18).plusDays(21), null));        
    }
    
    @Test
    public void testBirthdayMoreMax() {
        
        BirthdayRange parameters = createAnnotation(18, 50);
        validator.initialize(parameters);
        
        Assert.assertTrue(validator.isValid(LocalDate.now().minusYears(50).plusDays(1), null));
        Assert.assertFalse(validator.isValid(LocalDate.now().minusYears(50), null));        
    }    

    private BirthdayRange createAnnotation(int minValue, int maxValue) {
        AnnotationDescriptor<BirthdayRange> descriptor = new AnnotationDescriptor<BirthdayRange>(BirthdayRange.class);
        descriptor.setValue("min", minValue);
        descriptor.setValue("max", maxValue);

        return AnnotationFactory.create(descriptor);
    }
}
