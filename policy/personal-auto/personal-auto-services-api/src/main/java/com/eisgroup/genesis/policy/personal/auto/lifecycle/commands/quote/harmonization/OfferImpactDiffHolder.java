/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization;

import com.eisgroup.genesis.comparison.diff.Diff;

/**
 * Contains changes of an attribute for harmonization process.
 *
 * @author yratkevich
 * @since 9.16
 */
public class OfferImpactDiffHolder {
    public static final String NOT_SPECIFIED_MODEL = "<NOT_SPECIFIED_MODEL>";

    private String modelName;

    private String attribute;

    private Diff diff;

    public OfferImpactDiffHolder(String modelName, String attribute, Diff diff) {
        this.modelName = modelName;
        this.attribute = attribute;
        this.diff = diff;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Diff getDiff() {
        return diff;
    }

    public void setDiff(Diff diff) {
        this.diff = diff;
    }

    @Override
    public String toString() {
        return "OfferImpactDiffHolder{" +
                "modelName='" + modelName + '\'' +
                ", attribute='" + attribute + '\'' +
                ", diff=" + diff +
                '}';
    }
}