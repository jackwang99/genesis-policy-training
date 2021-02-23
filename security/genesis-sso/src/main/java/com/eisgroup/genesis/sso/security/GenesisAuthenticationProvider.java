/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.sso.security;

import com.eisgroup.genesis.security.model.UserByUsername;
import com.eisgroup.genesis.security.repository.SecurityRepository;
import io.reactivex.Maybe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.Objects;

/**
 * Authentication provider that authenticates Genesis users.
 * <p>
 * Steps:
 * 1) Check if username == password
 * 2) Load user from db by username
 * 3) Authorize the user from the DB information
 * <p>
 * NOTE: authentication is created from user name only.
 *
 * @author gvisokinskas
 */
public class GenesisAuthenticationProvider implements AuthenticationProvider {
    private static final UserByUsername EMPTY_USER = new UserByUsername();

    @Autowired
    private SecurityRepository securityRepository;

    @Value("${genesis.user.model}")
    private String userModelName;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (!Objects.equals(name, password)) {
            throw new BadCredentialsException("Bad credentials");
        }

        UserByUsername user = securityRepository.loadUserByUsername(name, userModelName)
                .firstElement()
                .switchIfEmpty(Maybe.just(EMPTY_USER))
                .blockingGet();

        if (Objects.equals(user, EMPTY_USER)) {
            throw new BadCredentialsException("Bad credentials");
        }

        return new UsernamePasswordAuthenticationToken(user.getUsername(), password, Collections.emptyList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}