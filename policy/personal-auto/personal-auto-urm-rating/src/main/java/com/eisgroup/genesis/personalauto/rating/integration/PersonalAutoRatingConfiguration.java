/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.personalauto.rating.integration;

import com.eisgroup.genesis.policy.core.rating.services.config.MockRuntimeContextBuilder;
import com.eisgroup.genesis.policy.core.rating.services.rateable.DefaultJsonReducer;
import com.eisgroup.genesis.policy.core.rating.services.rateable.DefaultRateableModelExtractor;
import com.eisgroup.rating.client.config.spi.RatingConfiguration;
import com.eisgroup.rating.client.params.RatingParameters;
import com.eisgroup.rating.client.params.impl.PreconfiguredUnifiedRatingParameters;
import com.eisgroup.rating.client.service.RateableModelExtractor;
import com.eisgroup.rating.client.service.RatingRuntimeContextBuilder;
import com.eisgroup.rating.client.workflow.api.WorkflowEngine;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rating configuration based on the calling remote Openl server for the Personal Auto product
 *
 * @author DLiauchuk on 8/1/17.
 */
public class PersonalAutoRatingConfiguration implements RatingConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(com.eisgroup.genesis.personalauto.rating.integration.PersonalAutoRatingConfiguration.class);

    private static final String RATING_SERVER_URL = "rating.server.url";
    private final String ratingServerUrl;

    public PersonalAutoRatingConfiguration() {
        ratingServerUrl = System.getProperty(RATING_SERVER_URL);
        if (StringUtils.isEmpty(ratingServerUrl)) {
            LOGGER.error("Cannot load 'rating.properties' for Personal Auto rating configuration");
        }
    }

    @Override
    public WorkflowEngine.NAME geWorkflowName() {
        return WorkflowEngine.NAME.RATING;
    }

    @Override
    public String getName() {
        return "PersonalAuto";
    }

    @Override
    public RateableModelExtractor getRateableModelExtractor() {
        DefaultRateableModelExtractor modelExtractor = new DefaultRateableModelExtractor();
        modelExtractor.setJsonReducer(new DefaultJsonReducer());
        return modelExtractor;
    }

    @Override
    public RatingRuntimeContextBuilder getRuntimeContextBuilder() {
        return new MockRuntimeContextBuilder();
    }

    @Override
    public RatingParameters getRatingParameters() {
        StringBuilder builder = new StringBuilder();
        builder.append(ratingServerUrl)
                .append("/com.eisgroup.genesis.personalauto.rating.RatingService");
        return new PreconfiguredUnifiedRatingParameters(builder.toString(),"rate");
    }
}
