/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.batch;

import com.eisgroup.genesis.commands.request.IdentifierRequest;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.google.gson.JsonObject;

/**
 * Defines a request to create a BAM record.
 * Currently contains only a user's {@link RootEntityKey} that will be used to load the user
 * from the repository.
 *
 * @author gvisokinskas
 */
public class BamCreateRequest extends IdentifierRequest {
    public BamCreateRequest(JsonObject original) {
        super(original);
    }

    public BamCreateRequest(RootEntityKey key) {
        super(key);
    }
}
