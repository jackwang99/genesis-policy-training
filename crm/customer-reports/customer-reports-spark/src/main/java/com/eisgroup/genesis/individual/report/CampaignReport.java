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

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.api.java.JavaDStream;

import com.datastax.spark.connector.japi.rdd.CassandraJavaRDD;
import com.eisgroup.genesis.factory.model.campaign.immutable.GenesisCampaign;
import com.eisgroup.genesis.individual.report.input.CampaignRow;
import com.eisgroup.genesis.individual.report.output.CampaignReportOutput;
import com.eisgroup.genesis.lifecycle.events.CommandExecutedEvent;
import com.eisgroup.genesis.report.ModelInfoFactory;
import com.eisgroup.genesis.report.SparkReport;
import com.eisgroup.genesis.report.util.CassandraQueries;

/**
 * Report that extracts campaign entries into hive
 *
 * @author mguzelis
 */
public class CampaignReport implements SparkReport<CampaignReportOutput> {
    private static final long serialVersionUID = -7445047777787841368L;

    private static final String MODEL_NAME = "Campaign";
    private static final String REPORT_NAME = "Customer";

    private static final Map<String, String> COLUMN_MAPPINGS = Collections.singletonMap("timestamp", "_timestamp");

    private String keyspaceName;
    private String tableName;

    @Override
    public void initialize(JavaSparkContext context) {
        ModelInfoFactory.ModelInfo campaignModel = ModelInfoFactory.resolveModelInfo(getModelName());
        keyspaceName = campaignModel.getSchemaName();
        tableName = campaignModel.getRootEntityName();
    }

    @Override
    public JavaRDD<CampaignReportOutput> processBatch(JavaSparkContext context, LocalDateTime startingPoint, LocalDateTime endingPoint) {
        // select
        CassandraJavaRDD<CampaignRow> campaignSelect = javaFunctions(context)
                .cassandraTable(keyspaceName, tableName, mapRowTo(CampaignRow.class, COLUMN_MAPPINGS))
                .select(CampaignRow.CASSANDRA_COLUMN_NAMES);

        // where
        JavaRDD<CampaignRow> campaigns = CassandraQueries.whereTimestamp(campaignSelect, startingPoint, endingPoint);

        return buildReport(campaigns);
    }

    @Override
    public JavaDStream<CampaignReportOutput> processRealtime(JavaSparkContext context, JavaDStream<CommandExecutedEvent> stream) {
        return stream.map(CommandExecutedEvent::getOutput)
                .transform(JavaRDD::cache)
                .transform(entityRDD -> buildReport(entityRDD // Campaign created
                        .filter(GenesisCampaign.class::isInstance)
                        .map(GenesisCampaign.class::cast)
                        .map(CampaignRow::new))
                );
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
    public Class<CampaignReportOutput> getReportType() {
        return CampaignReportOutput.class;
    }

    private JavaRDD<CampaignReportOutput> buildReport(JavaRDD<CampaignRow> campaigns) {
        return campaigns.map(CampaignReportOutput::new);
    }
}
