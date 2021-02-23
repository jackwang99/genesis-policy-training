/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.facade;

import com.eisgroup.genesis.exception.ErrorHolder;
import com.eisgroup.genesis.facade.module.EndpointPackage;
import com.eisgroup.genesis.facade.payload.Endpoint;
import com.eisgroup.genesis.facade.request.HttpOperationType;
import com.eisgroup.genesis.json.key.BaseKey;
import com.eisgroup.genesis.json.wrapper.DefaultJsonWrapperFactory;
import com.eisgroup.genesis.json.wrapper.JsonWrapperFactory;
import com.eisgroup.genesis.policy.core.facade.error.FacadeBuddyErrorDefinition;
import com.eisgroup.genesis.policy.core.facade.facadebuddy.flowdefinition.FlowDefinition;
import com.eisgroup.genesis.policy.core.facade.facadebuddy.flowexecutors.FlowOperationExecutor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.reactivex.Observable;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Option umbrella facade
 *
 * @author aspichakou
 */
public class OptionFacade implements EndpointPackage {

    private final JsonWrapperFactory jsonWrapper = new DefaultJsonWrapperFactory();

    @Autowired private FlowOperationExecutor flowOperationExecutor;

    @Override
    public String getName() {
        return "option";
    }

    @Endpoint(path = "/create/{rootId}/{revisionNo}", operationType = HttpOperationType.POST)
    public Observable<JsonObject> createFrom(OptionMemberRequest request) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.add(BaseKey.ATTRIBUTE_NAME, request.getKey().toJson());
        return flowOperationExecutor
            .execute(getFlowDefinition("pipeflows/createPlanVersion.json"), jsonObject);
    }

    @Endpoint(path = "/create", operationType = HttpOperationType.POST)
    public Observable<JsonObject> createWith(OptionMemberCarryingRequest request) {
        return flowOperationExecutor
            .execute(getFlowDefinition("pipeflows/writeAndCreatePlanVersion.json"),
                request.toJson());
    }

    @Endpoint(path = "/synchronize/{rootId}/{revisionNo}", operationType = HttpOperationType.POST)
    public Observable<JsonObject> synchronizeFrom(OptionMemberRequest request) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.add(BaseKey.ATTRIBUTE_NAME, request.getKey().toJson());

        return flowOperationExecutor
            .execute(getFlowDefinition("pipeflows/synchronizeOptionAndRate.json"), jsonObject);
    }

    @Endpoint(path = "/synchronize", operationType = HttpOperationType.POST)
    public Observable<JsonObject> synchronizeWith(OptionMemberCarryingRequest request) {
        return flowOperationExecutor
            .execute(getFlowDefinition("pipeflows/writeAndSynchronizeOptionAndRate.json"),
                request.toJson());
    }

    @Endpoint(path = "/rate", operationType = HttpOperationType.POST)
    public Observable<JsonObject> writeAndrate(OptionMemberCarryingRequest request) {
        return flowOperationExecutor
            .execute(getFlowDefinition("pipeflows/writeAndRate.json"), request.toJson());
    }

    private FlowDefinition getFlowDefinition(String flowName) {
        return jsonWrapper.wrap(load(flowName), FlowDefinition.class);
    }

    private JsonObject load(String resource) {
        try (final InputStreamReader reader = new InputStreamReader(getResourceAsStream(resource),
            StandardCharsets.UTF_8)) {
            final JsonParser parser = new JsonParser();
            return parser.parse(reader).getAsJsonObject();
        } catch (IOException e) {
            ErrorHolder errorHolder =
                FacadeBuddyErrorDefinition.FLOW_DEF_NOTFOUND.builder().params(resource).build();
            throw new FacadeFailureException(errorHolder, e);
        }
    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream is =
            Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (is == null) {
            ErrorHolder errorHolder =
                FacadeBuddyErrorDefinition.FLOW_DEF_NOTFOUND.builder().params(resource).build();
            throw new FacadeFailureException(errorHolder);
        }
        return is;
    }
}
