/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.personalhome.rating.integration;

import com.eisgroup.genesis.policy.core.rating.services.config.MockRuntimeContextBuilder;
import com.eisgroup.genesis.policy.core.rating.services.rateable.DefaultJsonReducer;
import com.eisgroup.genesis.policy.core.rating.services.rateable.DefaultRateableModelExtractor;
import com.eisgroup.rating.client.config.spi.RatingConfiguration;
import com.eisgroup.rating.client.params.RatingParameters;
import com.eisgroup.rating.client.params.impl.PreconfiguredUnifiedRatingParameters;
import com.eisgroup.rating.client.service.RateableModelExtractor;
import com.eisgroup.rating.client.service.RatingRuntimeContextBuilder;
import com.eisgroup.rating.client.workflow.api.WorkflowEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Rating configuration based on the calling remote Openl server for the Personal Home product
 *
 * @author zhchen
 * @since 1.0
 */
public class PersonalHomeRatingConfiguration implements RatingConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(com.eisgroup.genesis.personalhome.rating.integration.PersonalHomeRatingConfiguration.class);

    private static final String RATING_SERVER_URL = "rating.server.url";
    private static final String RATING_SERVER_PORT = "rating.server.port";

    private Properties properties;

    public PersonalHomeRatingConfiguration() {
        properties = new Properties();
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("personalHomeRating.properties")) {
            properties.load(is);
        } catch (IOException e) {
            LOGGER.error("Cannot load 'rating.properties' for Personal Home rating configuration", e);
        }
    }

    @Override
    public WorkflowEngine.NAME geWorkflowName() {
        return WorkflowEngine.NAME.RATING;
    }

    @Override
    public String getName() {
        return "PersonalHome";
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
        builder.append("http://")
                .append(properties.get(RATING_SERVER_URL))
                .append(":")
                .append(properties.get(RATING_SERVER_PORT))
                .append("/unified-rating-server-app/com.eisgroup.genesis.personalhome.rating.PersonalHomeRatingService");
        return new PreconfiguredUnifiedRatingParameters(builder.toString(),"rate");
    }
}
