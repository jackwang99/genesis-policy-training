/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.proto.report.ordering.error;

import com.eisgroup.genesis.exception.BaseErrorDefinition;

/**
 * Error definitions for MVR report ordering functionality.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class MVRReportOrderingErrorDefinition extends BaseErrorDefinition {

    /**
     * Error is shown when no report details are returned for person.
     */
    public static final MVRReportOrderingErrorDefinition REPORT_NOT_FOUND = new MVRReportOrderingErrorDefinition(
            "report0001", "There is no report for the requested person details");

    protected MVRReportOrderingErrorDefinition(String error, String message) {
        super(error, message);
    }

}
