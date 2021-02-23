/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.proto.report.ordering;

import com.eisgroup.genesis.json.AbstractJsonEntity;
import com.google.gson.JsonObject;

/**
 * DTO to carry data needed to order MVR Report.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class MVRReportOrderingData extends AbstractJsonEntity {

    private static final String PERSON_FIRST_NAME = "firstName";
    private static final String PERSON_LAST_NAME = "lastName";

    public MVRReportOrderingData(String firstName, String lastName) {
        super(new JsonObject());
        setString(PERSON_FIRST_NAME, firstName);
        setString(PERSON_LAST_NAME, lastName);
    }

    public String getPersonFirstName() {
        return getString(PERSON_FIRST_NAME);
    }

    public String getPersonLastName() {
        return getString(PERSON_LAST_NAME);
    }

}
