/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.proto.report.ordering;

import com.eisgroup.genesis.factory.model.mvrreport.impl.MVRReportFactory;
import com.eisgroup.genesis.factory.modeling.types.MVRReport;
import com.eisgroup.genesis.proto.report.ordering.error.MVRReportOrderingErrorDefinition;
import com.eisgroup.genesis.proto.report.ordering.error.ReportOrderingException;
import com.eisgroup.genesis.registry.report.constant.ReportRegistryConstants;
import com.eisgroup.genesis.registry.report.ordering.ReportOrderingService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.reactivex.Maybe;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Ordering service specific for motor vehicle service.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class MVRReportOrderingService implements ReportOrderingService<MVRReport, MVRReportOrderingData> {

    private static final String MVR_DATA_PATH = "META-INF/stubs/mvr-report-stub.json";
    private static final String REPORTS = "reports";

    @Override
    public Maybe<MVRReport> orderReport(MVRReportOrderingData data) {
        MVRReportFactory mvrReportFactory = new MVRReportFactory();

        for (JsonElement element : getReportData().getAsJsonArray(REPORTS)) {
            MVRReport mvrReport = convertToReport(element.getAsJsonObject(), mvrReportFactory);

            if (mvrReport.getFirstName().equalsIgnoreCase(data.getPersonFirstName())
                    && mvrReport.getLastName().equalsIgnoreCase(data.getPersonLastName())) {
                return Maybe.just(mvrReport);
            }
        }

        return Maybe.error(new ReportOrderingException(
                MVRReportOrderingErrorDefinition.REPORT_NOT_FOUND.builder().build()));
    }

    private MVRReport convertToReport(JsonObject report, MVRReportFactory mvrReportFactory) {
        report.addProperty("_type", "MVRReportEntity");
        report.addProperty("_modelName", "MVRReport");
        report.addProperty("_modelVersion", "1");

        return mvrReportFactory.fromJson(report);
    }

    @Override
    public String applicableFor() {
        return ReportRegistryConstants.MVR_REPORT;
    }

    private JsonObject getReportData() {
        JsonParser parser = new JsonParser();

        InputStream is = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(MVR_DATA_PATH);

        return parser.parse(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
    }

}
