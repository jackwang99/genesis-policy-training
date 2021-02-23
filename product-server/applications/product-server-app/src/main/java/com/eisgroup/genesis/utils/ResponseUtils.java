/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.utils;

import com.eisgroup.genesis.communication.protocol.ResponseEnvelope;
import com.eisgroup.genesis.exception.BaseErrorDefinition;
import com.eisgroup.genesis.exception.ErrorHolder;
import com.eisgroup.genesis.facade.EndpointFailure;

import static com.eisgroup.genesis.communication.ResponseMessage.getFailureInstance;
import static com.eisgroup.genesis.facade.EndpointFailure.fromError;

/**
 * Util methods to create responses
 *
 * @author dlevchuk
 * @since 10.9
 */
public final class ResponseUtils {

    private ResponseUtils() {}

    public static String createResponseEnvelopeBodyAsJson(EndpointFailure endpointFailure) {
        return new ResponseEnvelope<>(null, getFailureInstance(endpointFailure), true)
                .toJsonString();
    }

    public static String createResponseEnvelopeBodyAsJson(ErrorHolder errorHolder, int httpCode) {
        return createResponseEnvelopeBodyAsJson(fromError(errorHolder, httpCode));
    }

    public static String createResponseEnvelopeBodyAsJson(BaseErrorDefinition errorDefinition, Object... params) {
        EndpointFailure endpointFailure = fromError(errorDefinition.builder().params(params).build());

        return createResponseEnvelopeBodyAsJson(endpointFailure);
    }
}
