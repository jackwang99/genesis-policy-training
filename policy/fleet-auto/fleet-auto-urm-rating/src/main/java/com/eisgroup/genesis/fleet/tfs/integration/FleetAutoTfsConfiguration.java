/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.fleet.tfs.integration;

import com.eisgroup.genesis.policy.core.rating.services.config.MockRuntimeContextBuilder;
import com.eisgroup.genesis.policy.core.rating.services.rateable.tfs.TfsJsonReducer;
import com.eisgroup.genesis.policy.core.rating.services.rateable.tfs.TfsRateableModelExtractor;
import com.eisgroup.rating.client.config.spi.RatingConfiguration;
import com.eisgroup.rating.client.params.RatingParameters;
import com.eisgroup.rating.client.service.RateableModelExtractor;
import com.eisgroup.rating.client.service.RatingRuntimeContextBuilder;
import com.eisgroup.rating.client.workflow.api.WorkflowEngine;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TFS configuration based on the calling remote Openl server for the Fleet Auto product
 *
 * @author Andrey Arsatyants
 */
public class FleetAutoTfsConfiguration implements RatingConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(FleetAutoTfsConfiguration.class);

    private static final String TFS_SERVER_URL = "rating.server.url";
    private final String tfsServerUrl;

    public FleetAutoTfsConfiguration() {
        tfsServerUrl = System.getProperty(TFS_SERVER_URL);
        if (StringUtils.isEmpty(tfsServerUrl)) {
            LOGGER.error("Cannot load 'rating.properties' for Fleet Auto tfs configuration");
        }
    }

    @Override
    public WorkflowEngine.NAME geWorkflowName() {
        return WorkflowEngine.NAME.RATING;
    }

    @Override
    public String getName() {
        return "FleetAutoTfs";
    }

    @Override
    public RateableModelExtractor getRateableModelExtractor() {
        TfsRateableModelExtractor modelExtractor = new TfsRateableModelExtractor();
        modelExtractor.setTfsModelBuilder(new TfsJsonReducer());
        return modelExtractor;
    }

    @Override
    public RatingRuntimeContextBuilder getRuntimeContextBuilder() {
        return new MockRuntimeContextBuilder();
    }

    @Override
    public RatingParameters getRatingParameters() {
        StringBuilder builder = new StringBuilder();
        builder.append(tfsServerUrl)
                .append("/com.eisgroup.genesis.fleet.rating.RatingService");
        return new PreconfiguredUnifiedTfsParameters(builder.toString(), "calculateTaxesAndFees");
    }
}
