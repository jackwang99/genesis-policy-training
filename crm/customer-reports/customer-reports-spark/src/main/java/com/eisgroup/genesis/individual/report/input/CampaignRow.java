/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.individual.report.input;

import java.sql.Timestamp;
import java.util.Comparator;

import com.eisgroup.genesis.factory.model.campaign.immutable.GenesisCampaign;
import com.eisgroup.genesis.factory.modeling.types.immutable.AccessTrackInfo;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.report.IdentifiableRow;
import com.eisgroup.genesis.report.json.JsonObjectDelegate;

/**
 * DTO for reading from campaign cassandra table
 *
 * @author mguzelis
 */
public class CampaignRow implements IdentifiableRow<CampaignRow> {
    private static final long serialVersionUID = -6469951953159205900L;

    static {
        JsonObjectDelegate.registerConverter(AccessTrackInfo.class);
    }

    @SuppressWarnings("squid:S2386") // GENESIS-14408
    public static final String[] CASSANDRA_COLUMN_NAMES = new String[]{
            "rootId", "revisionNo", "_timestamp", "name", "state", "campaignId", "accessTrackInfo"
    };

    private String rootId;
    private Integer revisionNo;
    private Timestamp timestamp;
    private String name;
    private String state;
    private String campaignId;
    private JsonObjectDelegate<AccessTrackInfo> accessTrackInfo;

    public CampaignRow(String rootId, Integer revisionNo, Timestamp timestamp, String name, String state, String campaignId,
                       JsonObjectDelegate<AccessTrackInfo> accessTrackInfo) {
        this.rootId = rootId;
        this.revisionNo = revisionNo;
        this.timestamp = timestamp;
        this.name = name;
        this.state = state;
        this.campaignId = campaignId;
        this.accessTrackInfo = accessTrackInfo;
    }

    public CampaignRow(GenesisCampaign campaign) {
        RootEntityKey key = campaign.getKey();
        this.rootId = String.valueOf(key.getRootId());
        this.revisionNo = key.getRevisionNo();
        this.timestamp = campaign.getTimestamp()
                .map(Timestamp::valueOf)
                .orElse(null);

        this.name = campaign.getName();
        this.state = campaign.getState();
        this.campaignId = campaign.getCampaignId();
        this.accessTrackInfo = new JsonObjectDelegate(AccessTrackInfo.class, campaign.getAccessTrackInfo());
    }

    public String getRootId() {
        return rootId;
    }

    public Integer getRevisionNo() {
        return revisionNo;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public JsonObjectDelegate<AccessTrackInfo> getAccessTrackInfo() {
        return accessTrackInfo;
    }

    @Override
    public int compareTo(CampaignRow other) {
        Comparator<IdentifiableRow<?>> comparing = Comparator.comparing(IdentifiableRow::getRevisionNo);
        return comparing.compare(this, other);
    }
}
