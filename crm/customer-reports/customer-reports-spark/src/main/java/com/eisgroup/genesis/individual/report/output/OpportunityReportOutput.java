/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.individual.report.output;

import java.net.URI;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;

import com.eisgroup.genesis.factory.model.opportunity.immutable.GenesisEntityAssociation;
import com.eisgroup.genesis.factory.model.opportunity.immutable.GenesisOpportunityProductInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.AccessTrackInfo;
import com.eisgroup.genesis.individual.report.input.OpportunityRow;
import com.eisgroup.genesis.individual.report.input.OrganizationalPersonRow;
import com.eisgroup.genesis.report.ReportOutput.ReportRoot;
import com.eisgroup.genesis.report.ReportOutput.TableName;

/**
 * Hive structure for opportunity report output (root table)
 *
 * @author mguzelis
 */
@TableName("Opportunity")
public class OpportunityReportOutput extends ReportRoot {
    private static final long serialVersionUID = -3565985306071930678L;

    private static final String CUSTOMER = "Customer";

    private Timestamp updatedOn;
    private Timestamp createdOn;
    private String state;
    private String opportunityId;
    private String description;
    private String closeReason;
    private String channel;
    private String likelihood;
    private String owner;
    private Double potential;
    private String customerId;
    private String campaignId;
    private List<OpportunityProductsOutput> products = Collections.emptyList();
    private List<OpportunityEntitiesOutput> associations = Collections.emptyList();

    public OpportunityReportOutput(OpportunityRow opportunity, List<OrganizationalPersonRow> orgPersons) {
        this(opportunity.getRootId(), opportunity.getRevisionNo(), opportunity.getTimestamp(),
                opportunity.getAccessTrackInfo().getEntity(),
                opportunity.getProducts().getEntityList(),
                opportunity.getAssociations().getEntityList());

        this.state = opportunity.getState();
        this.opportunityId = opportunity.getOpportunityId();
        this.description = opportunity.getDescription();
        this.closeReason = opportunity.getCloseReason();
        this.channel = opportunity.getChannel();
        this.likelihood = opportunity.getLikelihood();
        this.potential = opportunity.getPotential();
        this.campaignId = opportunity.getCampaignId();

        this.owner = Optional.ofNullable(orgPersons)
                .filter(orgPersonRows -> !orgPersonRows.isEmpty())
                .map(orgPersonRows -> orgPersons.get(0).getSecurityIdentity())
                .orElse(null);
    }

    private OpportunityReportOutput(String rootId, Integer revisionNo, Timestamp timestamp,
                                    AccessTrackInfo accessTrackInfo,
                                    List<GenesisOpportunityProductInfo> products,
                                    List<GenesisEntityAssociation> associations) {
        this(rootId, revisionNo, timestamp);

        if (accessTrackInfo != null) {
            this.updatedOn = Timestamp.valueOf(accessTrackInfo.getUpdatedOn());
            this.createdOn = Timestamp.valueOf(accessTrackInfo.getCreatedOn());
        }

        if (CollectionUtils.isNotEmpty(products)) {
            this.products = createAggregateList(products,
                    product -> new OpportunityProductsOutput(this, product));
        }

        if (CollectionUtils.isNotEmpty(associations)) {
            this.associations = createAggregateList(associations,
                    entityAssociation -> {
                        if (CUSTOMER.equals(entityAssociation.getLink().getURI().getHost())) {
                            this.customerId = parseRootIdFromURI(entityAssociation.getLink().getURI());
                        }
                        return new OpportunityEntitiesOutput(this, entityAssociation);
                    }
            );
        }
    }

    private OpportunityReportOutput(String rootId, Integer revisionNo, Timestamp timestamp) {
        this.rootId = rootId;
        this.revisionNo = revisionNo;
        this.timestamp = Optional.ofNullable(timestamp)
                .orElse(null);
    }

    private String parseRootIdFromURI(URI uri) {
        String path = uri.getPath(); // i.e. "/INDIVIDUALCUSTOMER//89481b92-7d8b-4bc3-8c83-b2923327461e"
        return path.substring(path.lastIndexOf('/') + 1);
    }

    @Override
    public <T extends ReportAggregate> List<T> getAggregates(Class<T> aggregateType) {
        if (OpportunityProductsOutput.class.equals(aggregateType)) {
            return (List<T>) products;
        }
        if (OpportunityEntitiesOutput.class.equals(aggregateType)) {
            return (List<T>) associations;
        }
        throw new IllegalStateException("Unsupported aggregate type " + aggregateType);
    }

    public Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
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

    public String getOwner() {
        return owner;
    }

    public Double getPotential() {
        return potential;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCampaignId() {
        return campaignId;
    }
}
