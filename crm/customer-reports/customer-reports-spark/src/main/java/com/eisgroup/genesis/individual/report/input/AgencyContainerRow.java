/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws. 
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.eisgroup.genesis.individual.report.input;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

import com.eisgroup.genesis.factory.modeling.types.immutable.AgencyContainer;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.report.IdentifiableRow;

/**
 * DTO for reading from agency container tables
 * 
 * @author azukovskij
 *
 */
public class AgencyContainerRow implements IdentifiableRow<AgencyContainerRow> {
    
    private static final long serialVersionUID = 3524353423136279780L;
    
    public static final String[] COLUMN_NAMES = new String[] {
            "rootId", "revisionNo", "_timestamp", "agency"
    };

    private String rootId;
    private Integer revisionNo;
    private String agency;
    private Date timestamp;
    
    public AgencyContainerRow() {
    }
    
    public AgencyContainerRow(String rootId, Integer revisionNo, String agency, Date timestamp) {
        this.rootId = rootId;
        this.revisionNo = revisionNo;
        this.agency = agency;
        this.timestamp = timestamp;
    }

    public AgencyContainerRow(AgencyContainer agencyContainer) {
        RootEntityKey key = agencyContainer.getKey();
        this.rootId = String.valueOf(key.getRootId());
        this.revisionNo = key.getRevisionNo();
        this.agency = agencyContainer.getAgency();
        this.timestamp = agencyContainer.getTimestamp()
                .map(Timestamp::valueOf)
                .orElse(null);
    }
    
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

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public int compareTo(AgencyContainerRow other) {
        Comparator<IdentifiableRow<?>> comparing = Comparator.comparing(IdentifiableRow::getRevisionNo);
        return comparing.compare(this, other);
    }
    
}
