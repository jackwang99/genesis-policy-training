/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.groupinfo.deployer;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.eisgroup.genesis.security.SecurityToken;

/**
 * 
 * @author Dmitry Andronchik
 * @since 10.10
 */
public class DeployerAuthenticationToken extends AbstractAuthenticationToken {
    
    private static final long serialVersionUID = 1L;
    
    private final SecurityToken token;

    public DeployerAuthenticationToken(String tokenString, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        token = () -> tokenString;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
