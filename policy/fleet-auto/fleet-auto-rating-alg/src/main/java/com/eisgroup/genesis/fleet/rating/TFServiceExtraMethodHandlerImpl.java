/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.fleet.rating;

import com.eisgroup.genesis.tfs.services.api.impl.DefaultTfsCalculationResult;
import com.eisgroup.rating.converter.Converter;
import com.eisgroup.rating.converter.impl.StringToDateConverter;
import com.eisgroup.rating.converter.impl.StringToLocalDateConverter;
import com.eisgroup.rating.server.model.DestinationClassResolver;
import com.eisgroup.rating.server.model.Mapper;
import com.eisgroup.rating.server.model.impl.DefaultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Date;

/**
 * Interceptor that will handle the requests to the {@link RatingService#calculateTaxesAndFees(RatingRequest)}  method
 * <p>
 * Maps the income {@link com.eisgroup.rating.input.impl.DefaultBusinessNode} to the rating domain model Then calls the
 * method "CalculateTaxesAndFees" and passes the Policy object with runtime context to it
 *
 * @author Andrey Arsatyants
 * @since 2018M3
 */
public class TFServiceExtraMethodHandlerImpl
        implements org.openl.rules.ruleservice.core.annotations.ServiceExtraMethodHandler<DefaultTfsCalculationResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TFServiceExtraMethodHandlerImpl.class);
    private static final String FLEET_AUTO_POLICY_CLASS = "org.openl.generated.beans.fleet.Policy";

    @Override
    @SuppressWarnings("squid:S2658")
    public DefaultTfsCalculationResult invoke(final Method interfaceMethod, Object serviceBean, Object... args) throws Exception {
        DestinationClassResolver destinationClassResolver = s -> {
            try {
                return interfaceMethod.getDeclaringClass().getClassLoader().loadClass(FLEET_AUTO_POLICY_CLASS);
            } catch (ClassNotFoundException ex) {
                LOGGER.error("Can not find Auto Policy class: " + ex.getMessage(), ex);
            }
            return null;
        };

        Mapper mapper = resolveMapper(destinationClassResolver);
        RatingRequest request = (RatingRequest) args[0];
        Object policy = mapper.map(request.getNode(), FLEET_AUTO_POLICY_CLASS);

        return (DefaultTfsCalculationResult) serviceBean.getClass().getMethod("CalculateTaxesAndFees",
                interfaceMethod.getDeclaringClass().getClassLoader().loadClass("org.openl.rules.context.IRulesRuntimeContext"),
                interfaceMethod.getDeclaringClass().getClassLoader().loadClass(FLEET_AUTO_POLICY_CLASS))
                .invoke(serviceBean, request.getContext(), policy);
    }

    @SuppressWarnings("WeakerAccess")
    //visible for testing
    public Mapper resolveMapper(DestinationClassResolver destinationClassResolver) {
        DefaultMapper mapper = new DefaultMapper();
        mapper.registerConverter(String.class, Date.class, acceptTimeAsDate(new StringToDateConverter()));
        mapper.registerConverter(String.class, LocalDate.class, acceptTimeAsDate(new StringToLocalDateConverter()));
        mapper.setDestinationClassResolver(destinationClassResolver);
        return mapper;
    }

    private Converter acceptTimeAsDate(Converter delegate) {
        return convertible -> {
            convertible = ((String) convertible).substring(0, "yyyy-MM-dd".length());
            return delegate.convert(convertible);
        };
    }

}

