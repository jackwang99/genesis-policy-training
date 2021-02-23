/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.infra.reports;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapRowTo;

import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.api.java.JavaDStream;

import com.datastax.spark.connector.japi.rdd.CassandraJavaRDD;
import com.eisgroup.genesis.infra.reports.input.ActivityAggregateRow;
import com.eisgroup.genesis.infra.reports.output.BamReportOutput;
import com.eisgroup.genesis.lifecycle.events.CommandExecutedEvent;
import com.eisgroup.genesis.report.SparkReport;
import com.eisgroup.genesis.report.util.DateUtils;

/**
 * Report that extracts BAM activity entries into hive
 *
 * @author mguzelis
 */
public class BamReport implements SparkReport<BamReportOutput> {
    private static final long serialVersionUID = -7250674664093564217L;

    private static final String REPORT_NAME = "Activities";
    private static final String KEYSPACE_NAME = "activities";
    private static final String TABLE_NAME = "ActivityAggregate";
    private static final String MODEL_NAME = "Activity";

    @Override
    public JavaRDD<BamReportOutput> processBatch(JavaSparkContext context, LocalDateTime startingPoint, LocalDateTime endingPoint) {
        // select
        CassandraJavaRDD<ActivityAggregateRow> activitySelect = javaFunctions(context)
                .cassandraTable(KEYSPACE_NAME, TABLE_NAME, mapRowTo(ActivityAggregateRow.class))
                .select(ActivityAggregateRow.COLUMN_NAMES);

        // where
        JavaRDD<ActivityAggregateRow> activities = whereTimestamp(activitySelect, startingPoint, endingPoint);

        return buildReport(activities);
    }

    @Override
    public JavaDStream<BamReportOutput> processRealtime(JavaSparkContext context, JavaDStream<CommandExecutedEvent> stream) {
        throw new UnsupportedOperationException("Realtime processing of BAM activity events is not supported.");
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
    public Class<BamReportOutput> getReportType() {
        return BamReportOutput.class;
    }

    private JavaRDD<BamReportOutput> buildReport(JavaRDD<ActivityAggregateRow> activities) {
        return activities.map(BamReportOutput::new);
    }

    private CassandraJavaRDD<ActivityAggregateRow> whereTimestamp(CassandraJavaRDD<ActivityAggregateRow> selectStatement, LocalDateTime startingPoint, LocalDateTime endingPoint) {
        return Optional.ofNullable(startingPoint)
                .map(DateUtils::toJavaDate)
                .map(from -> Optional.ofNullable(endingPoint)
                        .map(DateUtils::toJavaDate)
                        .map(to -> selectStatement.where("\"timestamp\" > ? AND \"timestamp\" < ?", from, to)
                        )
                        .orElseGet(() -> selectStatement.where("\"timestamp\" > ?", from)
                        )
                ).orElse(selectStatement);
    }

}
