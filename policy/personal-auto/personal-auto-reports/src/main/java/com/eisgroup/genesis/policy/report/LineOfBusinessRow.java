/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.policy.report;

import java.io.Serializable;

/**
 * DTO for reading data of line of business from cassandra table
 *
 * @author mguzelis
 */
public class LineOfBusinessRow implements Serializable {
    private static final long serialVersionUID = -788731552116660222L;

    @SuppressWarnings("squid:S2386") // GENESIS-14408
    public static final String[] COLUMN_NAMES = new String[]{
            "parentId", "id", "lobCd"
    };

    private String parentId;
    private String id;
    private String lobCd;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLobCd() {
        return lobCd;
    }

    public void setLobCd(String lobCd) {
        this.lobCd = lobCd;
    }

}
