/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.modules;

import com.eisgroup.genesis.party.modules.PartyLocationCoreLifecycleModule;

/**
 * Party Location Lifecycle module implementation
 *
 * @author mslepikas
 */
public class PartyLocationLifecycleModule extends PartyLocationCoreLifecycleModule {

    @Override
    public String getModelName() {
        return "Location";
    }

}