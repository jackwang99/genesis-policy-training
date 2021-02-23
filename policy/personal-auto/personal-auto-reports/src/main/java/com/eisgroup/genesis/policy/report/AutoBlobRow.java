/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.policy.report;

import java.io.Serializable;

import com.eisgroup.genesis.json.key.BaseKey;

/**
 * DTO for reading from {@code AutoBlob} cassandra table
 *
 * @author mguzelis
 */
public class AutoBlobRow implements Serializable {
    private static final long serialVersionUID = -5347504309192454078L;

    @SuppressWarnings("squid:S2386") // GENESIS-14408
    public static final String[] COLUMN_NAMES = new String[]{
            BaseKey.ROOT_ID, BaseKey.ROOT_REVISION_NO, "blobCd"
    };

    private String rootId;
    private Integer revisionNo;
    private String blobCd;

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

    public String getBlobCd() {
        return blobCd;
    }

    public void setBlobCd(String blobCd) {
        this.blobCd = blobCd;
    }

}
