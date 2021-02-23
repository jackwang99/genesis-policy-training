/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.report.input;

import java.sql.Timestamp;
import java.util.Comparator;

import com.eisgroup.genesis.factory.model.organization.immutable.OrganizationEntity;
import com.eisgroup.genesis.factory.model.organization.immutable.OrganizationRoleEntity;
import com.eisgroup.genesis.factory.modeling.types.immutable.AccessTrackInfo;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.report.IdentifiableRow;
import com.eisgroup.genesis.report.json.JsonArrayDelegate;
import com.eisgroup.genesis.report.json.JsonObjectDelegate;

/**
 * DTO for reading data of organization from cassandra table
 *
 * @author mguzelis
 */
public class OrganizationRow implements IdentifiableRow<OrganizationRow> {
    private static final long serialVersionUID = -2127730019738702942L;

    static {
        JsonObjectDelegate.registerConverter(AccessTrackInfo.class);
        JsonArrayDelegate.registerConverter(OrganizationRoleEntity.class);
    }

    @SuppressWarnings("squid:S2386") // GENESIS-14408
    public static final String[] COLUMN_NAMES = new String[]{
            "rootId", "revisionNo", "_timestamp", "organizationCd", "accessTrackInfo", "assignedRoles"
    };

    private String rootId;
    private Integer revisionNo;
    private Timestamp timestamp;
    private String organizationCd;
    private JsonObjectDelegate<AccessTrackInfo> accessTrackInfo;
    private JsonArrayDelegate<OrganizationRoleEntity> assignedRoles;

    public OrganizationRow(String rootId, Integer revisionNo, Timestamp timestamp, String organizationCd,
                           JsonObjectDelegate<AccessTrackInfo> accessTrackInfo,
                           JsonArrayDelegate<OrganizationRoleEntity> assignedRoles) {
        this.rootId = rootId;
        this.revisionNo = revisionNo;
        this.timestamp = timestamp;
        this.organizationCd = organizationCd;
        this.accessTrackInfo = accessTrackInfo;
        this.assignedRoles = assignedRoles;
    }

    public OrganizationRow(OrganizationEntity organization) {
        RootEntityKey key = organization.getKey();
        this.rootId = String.valueOf(key.getRootId());
        this.revisionNo = key.getRevisionNo();
        this.timestamp = organization.getTimestamp()
                .map(Timestamp::valueOf)
                .orElse(null);

        this.organizationCd = organization.getOrganizationCd();
        this.accessTrackInfo = new JsonObjectDelegate(AccessTrackInfo.class, organization.getAccessTrackInfo());
        this.assignedRoles = new JsonArrayDelegate(OrganizationRoleEntity.class, organization.getAssignedRoles());
    }

    @Override
    public String getRootId() {
        return rootId;
    }

    @Override
    public Integer getRevisionNo() {
        return revisionNo;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getOrganizationCd() {
        return organizationCd;
    }

    public JsonObjectDelegate<AccessTrackInfo> getAccessTrackInfo() {
        return accessTrackInfo;
    }

    public JsonArrayDelegate<OrganizationRoleEntity> getAssignedRoles() {
        return assignedRoles;
    }

    @Override
    public int compareTo(OrganizationRow other) {
        Comparator<IdentifiableRow<?>> comparing = Comparator.comparing(IdentifiableRow::getRevisionNo);
        return comparing.compare(this, other);
    }
}
