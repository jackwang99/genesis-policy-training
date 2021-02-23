/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.eisgroup.genesis.jobs.batch.request;

import com.eisgroup.genesis.commands.request.EmptyRequest;
import com.google.gson.JsonObject;

public class BatchStartCampaignRequest extends EmptyRequest {
    public BatchStartCampaignRequest(JsonObject original) {
        super(original);
    }

    @Override
    public JsonObject getOriginal() {
        return super.getOriginal();
    }
}
