/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.report.quote;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.*;
import static com.eisgroup.genesis.report.util.CassandraQueries.*;
import static com.eisgroup.genesis.report.util.DateUtils.*;

import java.time.LocalDateTime;

import com.eisgroup.genesis.factory.model.personalauto.PersonalAutoPolicySummary;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.api.java.JavaDStream;

import com.datastax.spark.connector.japi.rdd.CassandraJavaRDD;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.json.key.BaseKey;
import com.eisgroup.genesis.lifecycle.events.CommandExecutedEvent;
import com.eisgroup.genesis.policy.report.PolicySummaryRow;
import com.eisgroup.genesis.policy.report.PremiumsRow;
import com.eisgroup.genesis.report.ModelInfoFactory;
import com.eisgroup.genesis.report.ModelInfoFactory.ModelInfo;
import com.eisgroup.genesis.report.SparkReport;

/**
 * {@link QuotePremiumsReportOutput} report implementation 
 * 
 * @author azukovskij
 *
 */
public class QuotePremiumsReport implements SparkReport<QuotePremiumsReportOutput> {

    private static final long serialVersionUID = -8687146203586672766L;
    
    private static final String MODEL_NAME = "PersonalAuto";
    
    protected String keyspaceName;
    
    protected String rootEntityType;

    @Override
    public Class<QuotePremiumsReportOutput> getReportType() {
        return QuotePremiumsReportOutput.class;
    }
    
    @Override
    public void initialize(JavaSparkContext context) {
        ModelInfo modelInfo = ModelInfoFactory.resolveModelInfo(getModelName());
        keyspaceName = modelInfo.getSchemaName("quote");
        rootEntityType = modelInfo.getRootEntityName();
    }
    
    @Override
    public String getModelName() {
        return MODEL_NAME;
    }
    
    @Override
    public JavaRDD<QuotePremiumsReportOutput> processBatch(JavaSparkContext context, LocalDateTime startingPoint, LocalDateTime endingPoint) {
        // select
        CassandraJavaRDD<PolicySummaryRow> quoteSelect = javaFunctions(context)
                .cassandraTable(keyspaceName, rootEntityType, mapRowTo(PolicySummaryRow.class))
                .select(PolicySummaryRow.COLUMN_NAMES)
                .where("\"_timestamp\"=?", toJavaDate(startingPoint));
        // where
        JavaRDD<PolicySummaryRow> quotes = whereTimestamp(quoteSelect, startingPoint, endingPoint)
                .filter(quote -> isRatedQuote(quote.getState()))
                .cache();
        return buildReport(quotes);
    }

    @Override
    public JavaDStream<QuotePremiumsReportOutput> processRealtime(JavaSparkContext context, JavaDStream<CommandExecutedEvent> stream) {
        return stream
                .filter(event -> event.getOutput() instanceof PolicySummary &&
                    "rated".equals(event.getTransition().getTargetState().getName()))
                .map(event -> new PolicySummaryRow(((PersonalAutoPolicySummary)event.getOutput())))
                .transform(this::buildReport);
    }

    /**
     * Checks if quote status {@link PolicySummary#getState()} is in rated state or above (has premium entries)
     * 
     * @param stateCd quote status
     * @return true if quote was rated
     */
    protected boolean isRatedQuote(String stateCd) {
        return "rated".equals(stateCd) ||
                "bound".equals(stateCd) ||
                "proposed".equals(stateCd);
    }

    protected JavaRDD<QuotePremiumsReportOutput> buildReport(JavaRDD<PolicySummaryRow> quotes) {
        return javaFunctions(quotes) 
                .joinWithCassandraTable(keyspaceName, "PremiumAggregate",
                        someColumns(BaseKey.ROOT_ID, BaseKey.ROOT_REVISION_NO, "premiumEntries"),
                        someColumns(BaseKey.ROOT_ID, BaseKey.ROOT_REVISION_NO), mapRowTo(PremiumsRow.class), mapToRow(PolicySummaryRow.class))
            .groupByKey()
            .map(tulpe -> new QuotePremiumsReportOutput(tulpe._1, tulpe._2));
    }

}
