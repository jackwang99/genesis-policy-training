/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.security.impl;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;
import java.util.Objects;

/**
 * Third party authentication token. Should be used in integration only.
 *
 * @author gvisokinskas
 */
public class ThirdPartyToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = -7560300773283864812L;

    private final String tokenValue;

    public ThirdPartyToken(String tokenValue) {
        super(Collections.emptyList());
        this.tokenValue = tokenValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ThirdPartyToken that = (ThirdPartyToken) o;
        return Objects.equals(tokenValue, that.tokenValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tokenValue);
    }

    @Override
    public String getCredentials() {
        return this.tokenValue;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
