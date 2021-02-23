/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.modules;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Valeriy Sizonenko
 */
@Configuration
@ComponentScan("com.eisgroup.genesis.commands.listener")
public class GenesisCampaignAssociationLifecycleModule extends CampaignAssociationLifecycleModule {

    @Override
    public String getModelName() {
        return "CampaignAssociation";
    }

    @Override
    public String getModelVersion() {
        return "1";
    }
}
