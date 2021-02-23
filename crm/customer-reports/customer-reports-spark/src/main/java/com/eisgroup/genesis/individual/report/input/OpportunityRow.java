/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.individual.report.input;

import java.sql.Timestamp;
import java.util.Comparator;

import com.eisgroup.genesis.factory.model.opportunity.immutable.GenesisEntityAssociation;
import com.eisgroup.genesis.factory.model.opportunity.immutable.GenesisOpportunity;
import com.eisgroup.genesis.factory.model.opportunity.immutable.GenesisOpportunityProductInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.AccessTrackInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.Owner;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.report.IdentifiableRow;
import com.eisgroup.genesis.report.json.JsonArrayDelegate;
import com.eisgroup.genesis.report.json.JsonObjectDelegate;

/**
 * DTO for reading from opportunity cassandra table
 *
 * @author mguzelis
 */
public class OpportunityRow implements IdentifiableRow<OpportunityRow> {
    private static final long serialVersionUID = -8315948779645283892L;

    static {
        JsonObjectDelegate.registerConverter(AccessTrackInfo.class);
        JsonArrayDelegate.registerConverter(GenesisOpportunityProductInfo.class);
        JsonArrayDelegate.registerConverter(GenesisEntityAssociation.class);
        JsonObjectDelegate.registerConverter(Owner.class);
    }

    @SuppressWarnings("squid:S2386") // GENESIS-14408
    public static final String[] CASSANDRA_COLUMN_NAMES = new String[]{
            "rootId", "revisionNo", "_timestamp", "state", "opportunityId", "description", "closeReason", "channel",
            "likelihood", "owner", "potential", "campaignId", "accessTrackInfo", "products", "associations"
    };

    private String rootId;
    private Integer revisionNo;
    private Timestamp timestamp;
    private String state;
    private String opportunityId;
    private String description;
    private String closeReason;
    private String channel;
    private String likelihood;
    private Double potential;
    private String campaignId;
    private JsonObjectDelegate<Owner> owner;
    private JsonObjectDelegate<AccessTrackInfo> accessTrackInfo;
    private JsonArrayDelegate<GenesisOpportunityProductInfo> products;
    private JsonArrayDelegate<GenesisEntityAssociation> associations;

    @SuppressWarnings("squid:S00107")
    public OpportunityRow(String rootId, Integer revisionNo, Timestamp timestamp, String state, String opportunityId,
                          String description, String closeReason, String channel, String likelihood,
                          Double potential, String campaignId,
                          JsonObjectDelegate<Owner> owner,
                          JsonObjectDelegate<AccessTrackInfo> accessTrackInfo,
                          JsonArrayDelegate<GenesisOpportunityProductInfo> products,
                          JsonArrayDelegate<GenesisEntityAssociation> associations) {
        this.rootId = rootId;
        this.revisionNo = revisionNo;
        this.timestamp = timestamp;
        this.state = state;
        this.opportunityId = opportunityId;
        this.description = description;
        this.closeReason = closeReason;
        this.channel = channel;
        this.likelihood = likelihood;
        this.owner = owner;
        this.potential = potential;
        this.campaignId = campaignId;
        this.accessTrackInfo = accessTrackInfo;
        this.products = products;
        this.associations = associations;
    }

    public OpportunityRow(GenesisOpportunity opportunity) {
        RootEntityKey key = opportunity.getKey();
        this.rootId = String.valueOf(key.getRootId());
        this.revisionNo = key.getRevisionNo();
        this.timestamp = opportunity.getTimestamp()
                .map(Timestamp::valueOf)
                .orElse(null);

        this.state = opportunity.getState();
        this.opportunityId = opportunity.getOpportunityId();
        this.description = opportunity.getDescription();
        this.closeReason = opportunity.getCloseReason();
        this.channel = opportunity.getChannel();
        this.likelihood = opportunity.getLikelihood();
        this.potential = opportunity.getPotential().doubleValue();
        this.campaignId = opportunity.getCampaignId();

        this.owner = new JsonObjectDelegate<>(Owner.class, opportunity.getOwner());
        this.accessTrackInfo = new JsonObjectDelegate<>(AccessTrackInfo.class, opportunity.getAccessTrackInfo());
        this.products = new JsonArrayDelegate(GenesisOpportunityProductInfo.class, opportunity.getProducts());
        this.associations = new JsonArrayDelegate(GenesisEntityAssociation.class, opportunity.getAssociations());
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

    public String getState() {
        return state;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public String getDescription() {
        return description;
    }

    public String getCloseReason() {
        return closeReason;
    }

    public String getChannel() {
        return channel;
    }

    public String getLikelihood() {
        return likelihood;
    }

    public JsonObjectDelegate<Owner> getOwner() {
        return owner;
    }

    public Double getPotential() {
        return potential;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public JsonObjectDelegate<AccessTrackInfo> getAccessTrackInfo() {
        return accessTrackInfo;
    }

    public JsonArrayDelegate<GenesisOpportunityProductInfo> getProducts() {
        return products;
    }

    public JsonArrayDelegate<GenesisEntityAssociation> getAssociations() {
        return associations;
    }

    @Override
    public int compareTo(OpportunityRow other) {
        Comparator<IdentifiableRow<?>> comparing = Comparator.comparing(IdentifiableRow::getRevisionNo);
        return comparing.compare(this, other);
    }
}
