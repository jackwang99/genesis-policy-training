/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.json.reducer.ref.app.services;

import com.eisgroup.genesis.decision.deployer.xlsx.XlsxDecisionDeployer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Deploy sample decision excel table data to inmemory storage column.
 *
 * @author ssauchuk
 * @since 10.2
 */
@Order(1)
@Component
public class DecisionTableDeployerRunner implements CommandLineRunner {

    private final Logger LOG = LoggerFactory.getLogger(DecisionTableDeployerRunner.class);

    @Autowired
    private XlsxDecisionDeployer xlsxDecisionDeployer;

    @Override
    public void run(String... args) {
        LOG.info("Deploy decision table data");
        xlsxDecisionDeployer.deploy();
    }
}
