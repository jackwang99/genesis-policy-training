/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.individual.report;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapRowTo;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.api.java.JavaDStream;

import com.datastax.spark.connector.japi.rdd.CassandraJavaRDD;
import com.eisgroup.genesis.factory.model.opportunity.immutable.GenesisOpportunity;
import com.eisgroup.genesis.individual.report.input.OpportunityRow;
import com.eisgroup.genesis.individual.report.input.OrganizationalPersonRow;
import com.eisgroup.genesis.individual.report.output.OpportunityEntitiesOutput;
import com.eisgroup.genesis.individual.report.output.OpportunityProductsOutput;
import com.eisgroup.genesis.individual.report.output.OpportunityReportOutput;
import com.eisgroup.genesis.json.link.EntityLink;
import com.eisgroup.genesis.lifecycle.events.CommandExecutedEvent;
import com.eisgroup.genesis.report.ModelInfoFactory;
import com.eisgroup.genesis.report.ModelInfoFactory.ModelInfo;
import com.eisgroup.genesis.report.ReportOutput;
import com.eisgroup.genesis.report.SparkReport;
import com.eisgroup.genesis.report.rdf.EntityLinkRow;
import com.eisgroup.genesis.report.rdf.EntityLinkRowResolver;
import com.eisgroup.genesis.report.util.CassandraQueries;

/**
 * Report that extracts opportunity entries into hive
 *
 * @author mguzelis
 */
public class OpportunityReport implements SparkReport<OpportunityReportOutput> {
    private static final long serialVersionUID = -1998902499825797742L;

    private static final String ORGANIZATIONAL_PERSON_MODEL = "OrganizationalPerson";
    private static final String MODEL_NAME = "Opportunity";
    private static final String REPORT_NAME = "Customer";

    private static final Map<String, String> COLUMN_MAPPINGS = Collections.singletonMap("timestamp", "_timestamp");

    private EntityLinkRowResolver<OrganizationalPersonRow> orgPersonLinkResolver;
    private String keyspaceName;
    private String tableName;

    @Override
    public void initialize(JavaSparkContext context) {
        ModelInfo opportunityModel = ModelInfoFactory.resolveModelInfo(getModelName());
        keyspaceName = opportunityModel.getSchemaName();
        tableName = opportunityModel.getRootEntityName();

        ModelInfo orgPersonModel = ModelInfoFactory.resolveModelInfo(ORGANIZATIONAL_PERSON_MODEL);
        orgPersonLinkResolver = new EntityLinkRowResolver<>(OrganizationalPersonRow.class, orgPersonModel.getSchemaName(), orgPersonModel.getRootEntityName());
    }

    @Override
    public JavaRDD<OpportunityReportOutput> processBatch(JavaSparkContext context, LocalDateTime startingPoint, LocalDateTime endingPoint) {
        // select
        CassandraJavaRDD<OpportunityRow> opportunitySelect = javaFunctions(context)
                .cassandraTable(keyspaceName, tableName, mapRowTo(OpportunityRow.class, COLUMN_MAPPINGS))
                .select(OpportunityRow.CASSANDRA_COLUMN_NAMES);

        // where
        JavaRDD<OpportunityRow> opportunities = CassandraQueries.whereTimestamp(opportunitySelect, startingPoint, endingPoint);

        return buildReport(opportunities);
    }

    @Override
    public JavaDStream<OpportunityReportOutput> processRealtime(JavaSparkContext context, JavaDStream<CommandExecutedEvent> stream) {
        return stream.map(CommandExecutedEvent::getOutput)
                .transform(JavaRDD::cache)
                .transform(entityRDD -> buildReport(entityRDD // Opportunity created
                        .filter(GenesisOpportunity.class::isInstance)
                        .map(GenesisOpportunity.class::cast)
                        .map(OpportunityRow::new)));
    }

    @Override
    public String getModelName() {
        return MODEL_NAME;
    }

    @Override
    public String getReportName() {
        return REPORT_NAME;
    }

    @Override
    public Class<OpportunityReportOutput> getReportType() {
        return OpportunityReportOutput.class;
    }

    @Override
    public Class<? extends ReportOutput.ReportAggregate>[] getReportAggregateTypes() {
        return new Class[]{OpportunityProductsOutput.class, OpportunityEntitiesOutput.class};
    }

    private JavaRDD<OpportunityReportOutput> buildReport(JavaRDD<OpportunityRow> opportunities) {
        JavaRDD<EntityLinkRow<OpportunityRow>> orgPersonLinks = opportunities.map(this::toLinkRow);
        return orgPersonLinkResolver.resolveLinks(orgPersonLinks, OrganizationalPersonRow.CASSANDRA_COLUMN_NAMES)
                .map(tuple -> new OpportunityReportOutput(tuple._1, tuple._2));
    }

    private EntityLinkRow<OpportunityRow> toLinkRow(OpportunityRow opportunity) {
        return Optional.ofNullable(opportunity.getOwner().getEntity())
                .flatMap(owner -> Optional.ofNullable(owner.getLink()))
                .map(EntityLink::getURIString)
                .filter(EntityLinkRow::isSupportedLink)
                .map(uriString -> new EntityLinkRow<>(uriString, opportunity))
                .orElseGet(() -> EntityLinkRow.emptyRef(opportunity));
    }
}
