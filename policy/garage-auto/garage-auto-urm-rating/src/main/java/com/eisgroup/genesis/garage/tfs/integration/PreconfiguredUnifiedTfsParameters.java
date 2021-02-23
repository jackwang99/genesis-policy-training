/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.garage.tfs.integration;

import com.eisgroup.rating.client.mapping.json.RatingJsonMapping;
import com.eisgroup.rating.client.params.impl.RatingInputs;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PreconfiguredUnifiedTfsParameters extends RatingJsonMapping {
    private static final String DEFAULT_RETURN_RESULT = "com.eisgroup.genesis.tfs.services.api.impl.DefaultTfsCalculationResult";


    public PreconfiguredUnifiedTfsParameters(String path, String methodPath) {
        super();

        Set<String> moduleTypes = new HashSet<>();
        moduleTypes.add(DEFAULT_RETURN_RESULT);
        this.setModuleTypes(moduleTypes);

        Set<String> overrideTypes = new HashSet<>();
        overrideTypes.add("org.openl.rules.variation.VariationsResult");
        this.setOverrideTypes(overrideTypes);

        this.setSupportVariations(false);
        this.setEnableDefaultTyping(false);
        this.setResultObjectClassName(DEFAULT_RETURN_RESULT);

        this.setJsonProperties(Map.of(
                RatingInputs.RUNTIME_CONTEXT.toString(), "context",
                RatingInputs.POLICY.toString(), "node")
        );

        this.setPath(path);
        this.setMethodPath(methodPath);
    }
}
