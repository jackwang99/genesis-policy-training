/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.validation.config;

import com.eisgroup.genesis.lifecycle.statemachine.config.StateMachineValidatorConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eisgroup.genesis.exception.ErrorHolder;
import com.eisgroup.genesis.lifecycle.statemachine.EntityRequiredKeyValidator;
import com.eisgroup.genesis.lifecycle.statemachine.StateHolder;
import com.eisgroup.genesis.validator.ValidationContext;

import io.reactivex.Observable;

/**
 * This is real CRM state machine validator configuration.
 * Because of StateMachine has limited functionality and completely doesn't fit to CRM business model.
 * In future all CRM models will without StateMachine. 
 * Before that we need to create this workaround.
 *  
 * @author avoitau
 *
 */
@AutoConfigureBefore(StateMachineValidatorConfig.class)
@Configuration
public class CRMStateMachineValidatorConfig {
    
    @Bean
    public EntityRequiredKeyValidator statefuleEntityRequiredKeyValidator() {
        return new EntityRequiredKeyValidator() {
            @Override
            public Observable<ErrorHolder> validate(StateHolder entity, ValidationContext context) {
                return Observable.empty();
            }
        };
    }
    
}
