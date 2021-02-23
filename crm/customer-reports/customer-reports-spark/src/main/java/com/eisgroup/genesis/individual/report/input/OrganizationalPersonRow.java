/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.individual.report.input;

import com.eisgroup.genesis.report.IdentifiableRow;

/**
 * DTO for reading the ext. link to the organizational person from org. struct domain.
 *
 * @author mguzelis
 */
public class OrganizationalPersonRow implements IdentifiableRow<OrganizationalPersonRow> {
    private static final long serialVersionUID = 7688448725906776528L;

    @SuppressWarnings("squid:S2386") // GENESIS-14408
    public static final String[] CASSANDRA_COLUMN_NAMES = new String[]{
            "rootId", "revisionNo", "securityIdentity"
    };

    private String rootId;
    private Integer revisionNo;
    private String securityIdentity;

    public OrganizationalPersonRow(String rootId, Integer revisionNo, String securityIdentity) {
        this.rootId = rootId;
        this.revisionNo = revisionNo;
        this.securityIdentity = securityIdentity;
    }

    @Override
    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    @Override
    public Integer getRevisionNo() {
        return revisionNo;
    }

    public void setRevisionNo(Integer revisionNo) {
        this.revisionNo = revisionNo;
    }

    public String getSecurityIdentity() {
        return securityIdentity;
    }

    public void setSecurityIdentity(String securityIdentity) {
        this.securityIdentity = securityIdentity;
    }
}
