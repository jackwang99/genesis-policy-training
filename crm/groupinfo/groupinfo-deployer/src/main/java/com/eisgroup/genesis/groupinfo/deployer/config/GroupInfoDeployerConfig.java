/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.groupinfo.deployer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eisgroup.genesis.groupinfo.deployer.GroupInfoDeployer;
import com.eisgroup.genesis.groupinfo.deployer.GroupInfoDeployerIndexer;
import com.eisgroup.genesis.crm.repository.CrmWriteRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

@Configuration
public class GroupInfoDeployerConfig {
    
    @Bean
    public GroupInfoDeployerIndexer groupInfoDeployerIndexer() {
        return new GroupInfoDeployerIndexer();       
    }
    
    @Bean
    public GroupInfoDeployer groupInfoDeployer(CrmWriteRepository writeRepo,
            GroupInfoDeployerIndexer indexer,
            @Value("${genesis.crm.groupinfos.deployment:classpath*:/groupinfos/*.json}") Resource[] groupInfos) {
        return new GroupInfoDeployer("GroupInfo", writeRepo, indexer, groupInfos);
    }
}
