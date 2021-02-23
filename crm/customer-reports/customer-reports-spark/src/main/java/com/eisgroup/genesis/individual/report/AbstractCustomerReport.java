/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws. 
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.eisgroup.genesis.individual.report;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.eisgroup.genesis.individual.report.input.AgencyContainerRow;
import com.eisgroup.genesis.individual.report.output.CustomerReportOutput;
import com.eisgroup.genesis.report.IdentifiableRow;
import com.eisgroup.genesis.report.ModelInfoFactory;
import com.eisgroup.genesis.report.ModelInfoFactory.ModelInfo;
import com.eisgroup.genesis.report.SparkReport;
import com.eisgroup.genesis.report.rdf.EntityLinkRow;
import com.eisgroup.genesis.report.rdf.EntityLinkRowResolver;
import com.eisgroup.genesis.report.rdf.RelationshipRepository;

abstract class AbstractCustomerReport implements SparkReport<CustomerReportOutput> {

    protected static final String AGENCY_CONTAINER_RELATIONSHIP = "agencyContainer_customer";

    private static final long serialVersionUID = -7598535156386595583L;

    private static final String REPORT_NAME = "Customer";
    
    protected static final Map<String, String> COLUMN_MAPPINGS = Collections.singletonMap("timestamp", "_timestamp");
    
    protected String keyspaceName;
    protected String tableName;
    
    protected RelationshipRepository relationshipRepo; 
    protected EntityLinkRowResolver<AgencyContainerRow> agencyContainerLinkResolver;
    
    @Override
    public void initialize(JavaSparkContext context) {
        ModelInfo customerModel = ModelInfoFactory.resolveModelInfo(getModelName());
        keyspaceName = customerModel.getSchemaName();
        tableName = customerModel.getRootEntityName();
        relationshipRepo = new RelationshipRepository(customerModel);
        
        ModelInfo agencyContainerModel = ModelInfoFactory.resolveModelInfo(getAgencyContainerModelName());
        agencyContainerLinkResolver = new EntityLinkRowResolver<>(AgencyContainerRow.class, 
                agencyContainerModel.getSchemaName(), agencyContainerModel.getRootEntityName());
    }
    
    @Override
    public String getReportName() {
        return REPORT_NAME;
    }
    
    @Override
    public String[] getAggregateModelNames() {
        return new String[] { getAgencyContainerModelName() };
    }
    
    protected abstract String getAgencyContainerModelName();

    @Override
    public Class<CustomerReportOutput> getReportType() {
        return CustomerReportOutput.class;
    }
    
    protected <R extends IdentifiableRow<R>> JavaPairRDD<R, List<AgencyContainerRow>> fetchAgencyContainerKeys(JavaRDD<R> customers) {
        JavaRDD<EntityLinkRow<R>> agencyContainerRefs = relationshipRepo.findByObject(AGENCY_CONTAINER_RELATIONSHIP, customers);
        return agencyContainerLinkResolver.resolveLinks(agencyContainerRefs, AgencyContainerRow.COLUMN_NAMES);
    }
    
    protected <R extends IdentifiableRow<R>> R latestRevision(List<R> elements) {
        return elements.stream()
                .sorted()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Empty revision list passed"));
    }

}
