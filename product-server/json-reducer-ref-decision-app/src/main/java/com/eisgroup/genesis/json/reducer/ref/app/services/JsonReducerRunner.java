/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.json.reducer.ref.app.services;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import com.eisgroup.genesis.decision.DecisionService;
import com.eisgroup.genesis.exception.InvocationError;
import com.eisgroup.genesis.json.reducer.EntityReducer;
import com.eisgroup.genesis.json.reducer.ref.model.AttributeTransformerDecisionTable;
import com.eisgroup.genesis.json.reducer.ref.model.TransformationExecutionContext;
import com.eisgroup.genesis.product.specification.general.Attribute;
import com.eisgroup.genesis.product.specification.general.Component;
import com.eisgroup.genesis.product.specification.general.Level;
import com.eisgroup.genesis.product.specification.general.connection.DefaultConnection;
import com.eisgroup.genesis.product.specification.policy.BusinessType;
import com.eisgroup.genesis.product.specification.policy.PolicyModel;
import com.eisgroup.genesis.product.specification.general.impl.AttributeImpl;
import com.eisgroup.genesis.product.specification.general.impl.DefaultComponent;
import com.eisgroup.genesis.product.specification.policy.impl.PolicyModelImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;


import static com.eisgroup.genesis.util.GsonUtil.getAsString;

/**
 * Example of json-reducer integration based on decision table.
 *
 * @author ssauchuk
 * @since 10.2
 */
@Order(2)
@org.springframework.stereotype.Component
public class JsonReducerRunner implements CommandLineRunner {

    private final Logger LOG = LoggerFactory.getLogger(JsonReducerRunner.class);

    protected static final String PERSON_AUTO_QUOTE_MODEL_JSON = "json/personAutoQuote.json";

    protected static final String PERSON_AUTO_SPECIFICATION_JSON = "json/personAutoPolicySpecification.json";

    private final DecisionService decisionService;

    public JsonReducerRunner(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @Override
    public void run(String... args) {

        EntityReducer<TransformationExecutionContext> entityReducer = EntityReducer.<TransformationExecutionContext>newBuilder()
                .withAttributeTransformator(new AttributeTransformerDecisionTable(decisionService)).build();

        JsonElement policySpecificationJson = PolicySpecificationDeserializer.resolvePolicySpecifications(
                new JsonParser().parse(readFile(PERSON_AUTO_SPECIFICATION_JSON))).get(0);

        PolicyModel filteredSpecification = PolicySpecificationDeserializer.fromJson(policySpecificationJson);

        TransformationExecutionContext context = new TransformationExecutionContext(filteredSpecification, "Rateable");

        JsonObject quote = (JsonObject) new JsonParser().parse(readFile(PERSON_AUTO_QUOTE_MODEL_JSON));

        JsonObject result = entityReducer.reduce(quote, context);

        Gson gsonOut = new GsonBuilder().setPrettyPrinting().create();

        LOG.info("Input quote:");
        LOG.info(gsonOut.toJson(quote));

        LOG.info("Policy specification:");
        LOG.info(gsonOut.toJson(policySpecificationJson));

        LOG.info("Json reducer result:");
        LOG.info(gsonOut.toJson(result));
    }

    protected static String readFile(String path) {
        try {
            return IOUtils.toString(
                    Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(path)),
                    Charset.forName("UTF8"));
        }
        catch (IOException e) {
            throw new InvocationError("check path configuration", e);
        }
    }

    /**
     * Deserialize json to {@link PolicyModelImpl} with a limited number of attributes.
     */
    protected static final class PolicySpecificationDeserializer {

        private static final Type POLICY_SPECIFICATION_TYPE = new TypeToken<PolicyModelImpl>() {
        }.getType();

        private static final Type DEFAULT_CONNECTIONS_TYPE = new TypeToken<List<DefaultConnection>>() {
        }.getType();

        private static final Type DEFAULT_COMPONENTS_TYPE = new TypeToken<List<DefaultComponent>>() {
        }.getType();

        private static final Type ATTRIBUTE_TYPE = new TypeToken<Attribute>() {
        }.getType();

        private static final Type BUSINESS_TYPE = new TypeToken<com.eisgroup.genesis.product.specification.general.Type>() {
        }.getType();

        public static JsonArray resolvePolicySpecifications(JsonElement json) {
            return json.getAsJsonObject().get("body").getAsJsonObject().get("success").getAsJsonObject().get("data")
                    .getAsJsonObject().get("policyProducts").getAsJsonArray();
        }

        public static PolicyModel fromJson(JsonElement jsonElement) {

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ATTRIBUTE_TYPE, getAttributeDeserializer())
                    .registerTypeAdapter(BUSINESS_TYPE, getBusinessTypeDeserializer())
                    .registerTypeAdapter(POLICY_SPECIFICATION_TYPE, getComponentsDeserializer())
                    .create();

            return gson.fromJson(jsonElement, POLICY_SPECIFICATION_TYPE);
        }

        private static JsonDeserializer<Attribute> getAttributeDeserializer() {
            return (json, typeOfT, context) -> {

                JsonObject obj = json.getAsJsonObject();

                return new AttributeImpl.Builder()
                        .id(getAsString(obj, "id"))
                        .businessName(getAsString(obj, "label"))
                        .designName(getAsString(obj, "businessName"))
                        .type(getAsString(obj, "type"))
                        .levelType(Optional.ofNullable(getAsString(obj, "levelType"))
                                .map(Level.LevelType::valueOf)
                                .orElse(null))
                        .connections(context.deserialize(obj.get("connections"), DEFAULT_CONNECTIONS_TYPE))
                        .build();
            };
        }

        private static JsonDeserializer<com.eisgroup.genesis.product.specification.general.Type> getBusinessTypeDeserializer() {
            return (json, typeOfT, context) -> Optional.ofNullable(getAsString(json.getAsJsonObject(), "name"))
                    .map(BusinessType::valueOf)
                    .orElse(null);
        }

        private static JsonDeserializer<PolicyModel> getComponentsDeserializer() {
            return (json, typeOfT, context) -> {
                List<Component> components = context
                        .deserialize(json.getAsJsonObject().get("components"), DEFAULT_COMPONENTS_TYPE);

                return new PolicyModelImpl.Builder()
                        .components(components)
                        .build();
            };
        }
    }
}
