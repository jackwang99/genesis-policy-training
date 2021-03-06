/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.garage.rating;

import com.eisgroup.rating.converter.Converter;
import com.eisgroup.rating.converter.impl.StringToDateConverter;
import com.eisgroup.rating.converter.impl.StringToLocalDateConverter;
import com.eisgroup.rating.output.RatingResult;
import com.eisgroup.rating.output.impl.DefaultRatingResult;
import com.eisgroup.rating.server.model.DestinationClassResolver;
import com.eisgroup.rating.server.model.Mapper;
import com.eisgroup.rating.server.model.impl.DefaultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Date;

/**
 * Interceptor that will handle the requests to the {@link RatingService#rate(RatingRequest)} method
 * <p>
 * Maps the income {@link com.eisgroup.rating.input.impl.DefaultBusinessNode} to the rating domain model Then calls the
 * method "DeterminePolicyPremium" and passes the Policy object with runtime context to it
 *
 * @author Denis Levchuk on 8/2/17.
 */
public class ServiceExtraMethodHandlerImpl
        implements org.openl.rules.ruleservice.core.annotations.ServiceExtraMethodHandler<RatingResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceExtraMethodHandlerImpl.class);
    private static final String GARAGE_AUTO_POLICY_CLASS = "org.openl.generated.beans.garage.Policy";

    @Override
    @SuppressWarnings("squid:S2658")
    public RatingResult invoke(final Method interfaceMethod, Object serviceBean, Object... args) throws Exception {
        DestinationClassResolver destinationClassResolver = s -> {
            try {
                return interfaceMethod.getDeclaringClass().getClassLoader().loadClass(GARAGE_AUTO_POLICY_CLASS);
            } catch (ClassNotFoundException ex) {
                LOGGER.error("Can not find Auto Policy class: " + ex.getMessage(), ex);
            }
            return null;
        };

        Mapper mapper = resolveMapper(destinationClassResolver);
        RatingRequest request = (RatingRequest) args[0];
        Object policy = mapper.map(request.getNode(), GARAGE_AUTO_POLICY_CLASS);

        return (DefaultRatingResult) serviceBean.getClass().getMethod("DeterminePolicyPremium",
                interfaceMethod.getDeclaringClass().getClassLoader().loadClass("org.openl.rules.context.IRulesRuntimeContext"),
                interfaceMethod.getDeclaringClass().getClassLoader().loadClass(GARAGE_AUTO_POLICY_CLASS))
                .invoke(serviceBean, request.getContext(), policy);
    }

    @SuppressWarnings("WeakerAccess")
        //visible for testing
    Mapper resolveMapper(DestinationClassResolver destinationClassResolver) {
        DefaultMapper mapper = new DefaultMapper();
        mapper.registerConverter(String.class, Date.class, acceptTimeAsDate(new StringToDateConverter()));
        mapper.registerConverter(String.class, LocalDate.class, acceptTimeAsDate(new StringToLocalDateConverter()));
        mapper.setDestinationClassResolver(destinationClassResolver);
        return mapper;
    }

    private Converter acceptTimeAsDate(Converter delegate) {
        return convertible -> {
            convertible = String.class.cast(convertible).substring(0, "yyyy-MM-dd".length());
            return delegate.convert(convertible);
        };
    }

}

