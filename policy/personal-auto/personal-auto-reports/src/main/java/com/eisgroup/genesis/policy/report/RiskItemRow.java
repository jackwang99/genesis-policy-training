/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.policy.report;

import java.io.Serializable;

/**
 * DTO for reading data of risk item from cassandra table
 *
 * @author mguzelis
 */
public class RiskItemRow implements Serializable {
    private static final long serialVersionUID = 2113158438836000172L;

    @SuppressWarnings("squid:S2386") // GENESIS-14408
    public static final String[] COLUMN_NAMES = new String[]{
            "rootId", "revisionNo", "parentId", "id"
    };

    private String rootId;
    private Integer revisionNo;
    private String parentId;
    private String id;

    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    public Integer getRevisionNo() {
        return revisionNo;
    }

    public void setRevisionNo(Integer revisionNo) {
        this.revisionNo = revisionNo;
    }

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

}
