/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.policy.report.policy;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import com.eisgroup.genesis.factory.model.personalauto.PersonalAutoPolicySummary;
import com.eisgroup.genesis.report.versioning.VersioningStrategy;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.api.java.JavaDStream;

import com.datastax.spark.connector.japi.rdd.CassandraJavaRDD;
import com.eisgroup.genesis.factory.modeling.types.immutable.PolicySummary;
import com.eisgroup.genesis.json.key.BaseKey;
import com.eisgroup.genesis.lifecycle.events.CommandExecutedEvent;
import com.eisgroup.genesis.policy.report.AutoBlobRow;
import com.eisgroup.genesis.policy.report.LineOfBusinessOutput;
import com.eisgroup.genesis.policy.report.LineOfBusinessRow;
import com.eisgroup.genesis.policy.report.PolicySummaryRow;
import com.eisgroup.genesis.policy.report.PremiumEntryOutput;
import com.eisgroup.genesis.policy.report.PremiumsRow;
import com.eisgroup.genesis.policy.report.RiskItemRow;
import com.eisgroup.genesis.report.ModelInfoFactory;
import com.eisgroup.genesis.report.ReportOutput;
import com.eisgroup.genesis.report.SparkReport;
import com.eisgroup.genesis.report.util.CassandraQueries;

/**
 * Report that extracts policy ({@code PersonalAuto}) transaction activity data into hive
 *
 * @author mguzelis
 */
public class PolicyTransactionActivityReport implements SparkReport<PolicyTransactionActivityReportOutput> {
    private static final long serialVersionUID = -6533015202462228053L;

    private static final String MODEL_NAME = "PersonalAuto";
    private static final String REPORT_NAME = "Policy";
    private static final String PREMIUM_AGGREGATE_TABLE = "PremiumAggregate";
    private static final String AUTO_LOB_TABLE = "AutoLOB";
    private static final String AUTO_VEHICLE_TABLE = "AutoVehicle";
    private static final String AUTO_BLOB_TABLE = "AutoBLOB";
    private static final Map<String, String> COLUMN_MAPPINGS = Collections.singletonMap("timestamp", "_timestamp");
    private static final String[] CASSANDRA_COLUMN_NAMES = new String[]{
            "rootId", "revisionNo", "policyNumber", "state", "accessTrackInfo", "transactionDetails",
            "_timestamp", "productCd", "riskStateCd", "businessDimensions", "termDetails", "customer",
            "_variation"
    };

    private String keyspaceName;
    private String tableName;

    @Override
    public void initialize(JavaSparkContext context) {
        ModelInfoFactory.ModelInfo modelInfo = ModelInfoFactory.resolveModelInfo(getModelName());
        keyspaceName = modelInfo.getSchemaName("policy");
        tableName = modelInfo.getRootEntityName();
    }

    @Override
    public JavaRDD<PolicyTransactionActivityReportOutput> processBatch(JavaSparkContext context,
                                                                       LocalDateTime startingPoint, LocalDateTime endingPoint) {
        // select
        CassandraJavaRDD<PolicySummaryRow> policySelect = javaFunctions(context)
                .cassandraTable(keyspaceName, tableName, mapRowTo(PolicySummaryRow.class, COLUMN_MAPPINGS))
                .select(CASSANDRA_COLUMN_NAMES);

        // where
        JavaRDD<PolicySummaryRow> policies = CassandraQueries.whereTimestamp(policySelect, startingPoint, endingPoint);

        return buildReport(policies);
    }
    
    @Override
    public JavaDStream<PolicyTransactionActivityReportOutput> processRealtime(JavaSparkContext context, JavaDStream<CommandExecutedEvent> stream) {
        return stream
                .filter(event -> event.getOutput() instanceof PolicySummary)
                .map(event -> new PolicySummaryRow(((PersonalAutoPolicySummary) event.getOutput())))
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
    public Class<PolicyTransactionActivityReportOutput> getReportType() {
        return PolicyTransactionActivityReportOutput.class;
    }

    @Override
    public Class<? extends ReportOutput.ReportAggregate>[] getReportAggregateTypes() {
        return new Class[]{LineOfBusinessOutput.class, PremiumEntryOutput.class};
    }

    private JavaRDD<PolicyTransactionActivityReportOutput> buildReport(JavaRDD<PolicySummaryRow> policies) {

        // blob
        JavaPairRDD<PolicySummaryRow, Iterable<AutoBlobRow>> joinedWithBlob = javaFunctions(policies)
                .joinWithCassandraTable(keyspaceName, AUTO_BLOB_TABLE,
                        someColumns(AutoBlobRow.COLUMN_NAMES),
                        someColumns(BaseKey.ROOT_ID, BaseKey.ROOT_REVISION_NO),
                        mapRowTo(AutoBlobRow.class), mapToRow(PolicySummaryRow.class))
                .groupByKey();

        // premiumEntries
        JavaPairRDD<PolicySummaryRow, Iterable<PremiumsRow>> joinedWithPremiumRows = javaFunctions(policies)
                .joinWithCassandraTable(keyspaceName, PREMIUM_AGGREGATE_TABLE,
                        someColumns(PremiumsRow.COLUMN_NAMES),
                        someColumns(BaseKey.ROOT_ID, BaseKey.ROOT_REVISION_NO),
                        mapRowTo(PremiumsRow.class), mapToRow(PolicySummaryRow.class))
                .groupByKey();

        // lobs
        JavaPairRDD<PolicySummaryRow, Iterable<LineOfBusinessRow>> joinedWithLobs = javaFunctions(policies)
                .joinWithCassandraTable(keyspaceName, AUTO_LOB_TABLE,
                        someColumns(LineOfBusinessRow.COLUMN_NAMES),
                        someColumns(BaseKey.ROOT_ID, BaseKey.ROOT_REVISION_NO),
                        mapRowTo(LineOfBusinessRow.class), mapToRow(PolicySummaryRow.class))
                .groupByKey();

        // riskItems
        JavaPairRDD<PolicySummaryRow, Iterable<RiskItemRow>> joinedWithRiskItems = javaFunctions(policies)
                .joinWithCassandraTable(keyspaceName, AUTO_VEHICLE_TABLE,
                        someColumns(RiskItemRow.COLUMN_NAMES),
                        someColumns(BaseKey.ROOT_ID, BaseKey.ROOT_REVISION_NO),
                        mapRowTo(RiskItemRow.class), mapToRow(PolicySummaryRow.class))
                .groupByKey();

        return joinedWithBlob
                .join(joinedWithPremiumRows)
                .join(joinedWithLobs)
                .join(joinedWithRiskItems)
                .map(tuple -> new PolicyTransactionActivityReportOutput(tuple._1, tuple._2._1._1._1, tuple._2._1._1._2, tuple._2._1._2, tuple._2._2));
    }

}
