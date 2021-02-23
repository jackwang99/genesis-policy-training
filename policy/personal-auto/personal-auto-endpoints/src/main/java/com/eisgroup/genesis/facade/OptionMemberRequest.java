/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.facade;

import com.eisgroup.genesis.facade.request.PathParam;
import com.eisgroup.genesis.facade.request.ValueProvider;
import com.eisgroup.genesis.facade.request.ValueSupplier;
import com.eisgroup.genesis.json.JsonEntity;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.model.request.ModelAware;

import java.util.UUID;

/**
 * Option member key request
 *
 * @author aspichakou
 */
public interface OptionMemberRequest extends JsonEntity, ModelAware {
    @ValueProvider(RootEntityKeyParser.class)
    RootEntityKey getKey();

    public class RootEntityKeyParser implements ValueSupplier<RootEntityKey> {

        protected final RootEntityKey value;

        public RootEntityKeyParser(@PathParam("rootId") UUID rootId,
            @PathParam("revisionNo") Integer revisionNo) {
            this.value = new RootEntityKey(rootId, revisionNo);
        }

        @Override
        public RootEntityKey getValue() {
            return value;
        }

    }
}
