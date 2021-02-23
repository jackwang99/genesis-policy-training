/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.report;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapRowTo;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.api.java.JavaDStream;

import com.datastax.spark.connector.japi.rdd.CassandraJavaRDD;
import com.eisgroup.genesis.factory.model.organization.immutable.OrganizationEntity;
import com.eisgroup.genesis.lifecycle.events.CommandExecutedEvent;
import com.eisgroup.genesis.report.ModelInfoFactory.ModelInfo;
import com.eisgroup.genesis.report.input.OrganizationRow;
import com.eisgroup.genesis.report.output.OrganizationReportOutput;
import com.eisgroup.genesis.report.output.UnderwritingCompanyOutput;
import com.eisgroup.genesis.report.util.CassandraQueries;

/**
 * Report that extracts Organization entries into hive
 *
 * @author mguzelis
 */
public class OrganizationReport implements SparkReport<OrganizationReportOutput> {
    private static final long serialVersionUID = -6599967051854865365L;

    private static final String MODEL_NAME = "Organization";
    private static final String REPORT_NAME = "OrgStruct";
    private static final Map<String, String> COLUMN_MAPPINGS = Collections.singletonMap("timestamp", "_timestamp");

    private String keyspaceName;
    private String tableName;

    @Override
    public void initialize(JavaSparkContext context) {
        ModelInfo organizationModel = ModelInfoFactory.resolveModelInfo(getModelName());
        keyspaceName = organizationModel.getSchemaName();
        tableName = organizationModel.getRootEntityName();
    }

    @Override
    public JavaRDD<OrganizationReportOutput> processBatch(JavaSparkContext context, LocalDateTime startingPoint, LocalDateTime endingPoint) {
        // select
        CassandraJavaRDD<OrganizationRow> organizationSelect = javaFunctions(context)
                .cassandraTable(keyspaceName, tableName, mapRowTo(OrganizationRow.class, COLUMN_MAPPINGS))
                .select(OrganizationRow.COLUMN_NAMES);

        // where
        JavaRDD<OrganizationRow> organizations = CassandraQueries.whereTimestamp(organizationSelect, startingPoint, endingPoint);

        return buildReport(organizations);
    }

    @Override
    public JavaDStream<OrganizationReportOutput> processRealtime(JavaSparkContext context, JavaDStream<CommandExecutedEvent> stream) {
        return stream
                .filter(event -> event.getOutput() instanceof OrganizationEntity)
                .map(event -> new OrganizationRow((OrganizationEntity) event.getOutput()))
                .transform(this::buildReport);
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
    public Class<OrganizationReportOutput> getReportType() {
        return OrganizationReportOutput.class;
    }

    @Override
    public Class<? extends ReportOutput.ReportAggregate>[] getReportAggregateTypes() {
        return new Class[]{UnderwritingCompanyOutput.class};
    }

    private JavaRDD<OrganizationReportOutput> buildReport(JavaRDD<OrganizationRow> organizations) {
        return organizations.map(OrganizationReportOutput::new);
    }
}
