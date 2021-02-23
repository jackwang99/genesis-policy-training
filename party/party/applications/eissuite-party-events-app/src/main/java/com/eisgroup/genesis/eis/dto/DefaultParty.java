/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.eis.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dlevchuk
 */
public abstract class DefaultParty implements EISParty {

    private References references;
    private Relationship[] relationships;
    private Map<String, String> attributes = new HashMap<>();
    private String partyType;

    public DefaultParty(References references, Relationship[] relationships, Map<String, String> attributes, String partyType) {
        this.references = references;
        this.relationships = relationships;
        this.attributes.putAll(attributes);
        this.partyType = partyType;
    }

    public DefaultParty() {
    }

    public void setPartyType(String partyType) {
        this.partyType = partyType;
    }

    @Override
    public String getPartyType() {
        return partyType;
    }

    public void setReferences(References references) {
        this.references = references;
    }

    public void setRelationships(Relationship[] relationships) {
        this.relationships = relationships;
    }

    @Override
    public References getReferences() {
        return references;
    }

    @Override
    public Relationship[] getRelationships() {
        return relationships;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
