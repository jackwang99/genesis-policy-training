/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.sso.security;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * An implementation of the Spring's {@link UserAuthenticationConverter}
 * that adds SUB field to the generated token.
 * This class implicitly assumes that no authorities exist for authenticated users.
 *
 * @author gvisokinskas
 */
public final class GenesisAuthenticationConverter implements UserAuthenticationConverter {
    private static final String SUB = "sub";

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Autowired(required = false)
    private Optional<UserDetailsService> userDetailsService;

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication userAuthentication) {
        return ImmutableMap.of(SUB, userAuthentication.getName());
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        return Optional.ofNullable(map.get(SUB))
                .map(String.class::cast)
                .map(name -> userDetailsService
                        .map(service -> service.loadUserByUsername(name))
                        .map(Object.class::cast)
                        .orElse(name))
                .map(principal -> new UsernamePasswordAuthenticationToken(principal, "N/A", Collections.emptyList()))
                .orElse(null);
    }
}