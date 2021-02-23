/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.proto.report.ordering.error;

import com.eisgroup.genesis.exception.BaseErrorException;
import com.eisgroup.genesis.exception.ErrorHolder;

/**
 * Exception to be thrown when report ordering cannot be performed
 * or completed.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class ReportOrderingException extends BaseErrorException {

    private static final long serialVersionUID = -7334389687474383927L;

    public ReportOrderingException(ErrorHolder error) {
        super(error);
    }

}
