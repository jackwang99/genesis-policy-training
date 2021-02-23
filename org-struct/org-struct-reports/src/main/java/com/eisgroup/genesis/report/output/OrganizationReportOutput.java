/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.report.output;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.eisgroup.genesis.factory.model.organization.immutable.OrganizationRoleEntity;
import com.eisgroup.genesis.factory.modeling.types.immutable.AccessTrackInfo;
import com.eisgroup.genesis.report.ReportOutput.ReportRoot;
import com.eisgroup.genesis.report.ReportOutput.TableName;
import com.eisgroup.genesis.report.input.OrganizationRow;
import com.eisgroup.genesis.report.json.JsonArrayDelegate;

/**
 * Hive structure for organization report output (root table)
 *
 * @author mguzelis
 */
@TableName("Organization")
public class OrganizationReportOutput extends ReportRoot {
    private static final long serialVersionUID = 1844951225852752928L;

    private static final String UNDERWRITING_COMPANY = "UnderwritingCompany";

    private String organizationCd;
    private Timestamp updatedOn;

    private List<UnderwritingCompanyOutput> underwritingCompanies = Collections.emptyList();

    public OrganizationReportOutput(OrganizationRow organization) {
        this(organization.getRootId(), organization.getRevisionNo(), organization.getTimestamp());
        this.organizationCd = organization.getOrganizationCd();

        AccessTrackInfo accessTrackInfo = organization.getAccessTrackInfo().getEntity();
        if (accessTrackInfo != null) {
            this.updatedOn = Timestamp.valueOf(accessTrackInfo.getUpdatedOn());
        }

        this.underwritingCompanies = Optional.ofNullable(organization.getAssignedRoles())
                .map(JsonArrayDelegate::getEntityList)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(role -> UNDERWRITING_COMPANY.equals(role.getRoleTypeCd()))
                .map(OrganizationRoleEntity::getRoleDetails)
                .map(organizationRoleDetails -> new UnderwritingCompanyOutput(this, organizationRoleDetails))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("squid:UnusedPrivateMethod")
    private OrganizationReportOutput(String rootId, Integer revisionNo, Timestamp timestamp) {
        this.rootId = rootId;
        this.revisionNo = revisionNo;
        this.timestamp = Optional.ofNullable(timestamp)
                .orElse(null);
    }

    @Override
    public <T extends ReportAggregate> List<T> getAggregates(Class<T> aggregateType) {
        if (UnderwritingCompanyOutput.class.equals(aggregateType)) {
            return (List<T>) underwritingCompanies;
        }
        throw new IllegalStateException("Unsupported aggregate type " + aggregateType);
    }

    public String getOrganizationCd() {
        return organizationCd;
    }

    public Timestamp getUpdatedOn() {
        return updatedOn;
    }
}
