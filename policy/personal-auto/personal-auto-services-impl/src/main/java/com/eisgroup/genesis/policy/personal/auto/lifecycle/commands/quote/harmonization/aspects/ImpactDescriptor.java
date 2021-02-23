/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.aspects;

import com.eisgroup.genesis.decision.RowResult;
import com.eisgroup.genesis.decision.entity.targets.AttributeAspect;
import com.eisgroup.genesis.decision.entity.targets.EntityAspect;

import java.util.Objects;
import java.util.Optional;

/**
 * Holds information about path of impacted attribute and all correspondent data from impact table
 *
 * @author sbelauski
 */
public class ImpactDescriptor {

    public static final String IMPACT_TABLE_NAME = "Impact table";

    private final String entityName;
    private final String entityAttribute;
    private final String path;
    private final Boolean isEmpty;
    private final String resetValue;

    public ImpactDescriptor(String entityName, String entityAttribute, Boolean isEmpty, String resetValue, String path) {
        this.entityName = entityName;
        this.entityAttribute = entityAttribute;
        this.isEmpty = isEmpty;
        this.resetValue = resetValue;
        this.path = path;
    }

    public ImpactDescriptor(RowResult row, String path) {
        this((String) row.get(EntityAspect.ENTITY), (String) row.get(AttributeAspect.ATTRIBUTE),
                (Boolean) row.get(IsEmptyAspect.NAME), (String) row.get(ResetValueAspect.NAME), path);
    }

    public String getEntityName() {
        return entityName;
    }

    public String getEntityAttribute() {
        return entityAttribute;
    }

    public String getPath() {
        return path;
    }

    public Optional<Boolean> isEmpty() {
        return Optional.ofNullable(isEmpty);
    }

    public Optional<String> getResetValue() {
        return Optional.ofNullable(resetValue);
    }

    @Override
    @SuppressWarnings("squid:S1067")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImpactDescriptor that = (ImpactDescriptor) o;
        return Objects.equals(entityName, that.entityName) &&
                Objects.equals(entityAttribute, that.entityAttribute) &&
                Objects.equals(path, that.path) &&
                Objects.equals(isEmpty, that.isEmpty) &&
                Objects.equals(resetValue, that.resetValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityName, entityAttribute, isEmpty, resetValue, path);
    }
}
