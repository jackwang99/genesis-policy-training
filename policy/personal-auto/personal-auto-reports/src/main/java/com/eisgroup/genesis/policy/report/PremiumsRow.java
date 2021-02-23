/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.report;

import java.io.Serializable;

import com.eisgroup.genesis.json.key.BaseKey;
import com.eisgroup.genesis.report.json.JsonArrayDelegate;

/**
 * DTO for reading premiums from cassandra table
 */
public class PremiumsRow implements Serializable {
    private static final long serialVersionUID = -9110677750024731071L;

    @SuppressWarnings("squid:S2386") // GENESIS-14408
    public static final String[] COLUMN_NAMES = new String[]{
            BaseKey.ROOT_ID, BaseKey.ROOT_REVISION_NO, "premiumEntries"
    };

    static {
        JsonArrayDelegate.registerConverter(PremiumEntry.class);
    }

    private String rootId;
    private Integer revisionNo;
    private JsonArrayDelegate<PremiumEntry> premiumEntries;

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

    public JsonArrayDelegate<PremiumEntry> getPremiumEntries() {
        return premiumEntries;
    }

    public void setPremiumEntries(JsonArrayDelegate<PremiumEntry> premiumEntries) {
        this.premiumEntries = premiumEntries;
    }

}
