/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.security.impl;

import com.eisgroup.genesis.security.server.api.AuthenticationResolver;
import org.springframework.security.core.Authentication;

/**
 * Authentication resolver for third-party authentication token.
 *
 * @author dstulgis
 */
public class ThirdPartyAuthenticationResolver implements AuthenticationResolver {

    private static final String HEADER_PREFIX = "Bearer ";

    @Override
    public boolean supports(String authHeader) {
        return authHeader != null && authHeader.startsWith(HEADER_PREFIX);
    }

    @Override
    public Authentication resolve(String authHeader) {
        return new ThirdPartyToken(authHeader.substring(HEADER_PREFIX.length()));
    }

}
