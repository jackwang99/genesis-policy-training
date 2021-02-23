/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.groupinfo.deployer;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import com.eisgroup.genesis.crm.repository.CrmWriteRepository;
import com.eisgroup.genesis.deployer.Deployable;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.modeling.types.GroupInfo;
import com.eisgroup.genesis.security.GenesisAuthority;
import com.eisgroup.genesis.security.escalation.EscalatedAccessRunner;
import com.eisgroup.genesis.security.escalation.EscalatedDimensionToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Used for deploying GroupInfo entities.
 * 
 * @author Dmitry Andronchik
 * @since 10.10
 */
@Order
public class GroupInfoDeployer implements Deployable {

    protected final static Logger logger = LoggerFactory.getLogger(GroupInfoDeployer.class);
    
    protected static final String GROUP_INFO_BUSINESS_KEY = "groupId";

    private Resource[] groupInfosJson;

    private final String groupInfoModelName;
    
    private final CrmWriteRepository writeRepo;
    private final GroupInfoDeployerIndexer indexer;
    
    public GroupInfoDeployer(String groupInfoModelName, CrmWriteRepository writeRepo,
            GroupInfoDeployerIndexer indexer, Resource[] groupInfosJson) {
        this.groupInfoModelName = groupInfoModelName;
        this.writeRepo = writeRepo;
        this.groupInfosJson = groupInfosJson;
        this.indexer = indexer;
    }

    @Override
    public String getName() {
        return "groupinfo";
    }

    @Override
    public void deploy() {
        logger.info("~~~ Starting GroupInfo deploying");
        
        AbstractAuthenticationToken authenticationToken = new DeployerAuthenticationToken("", 
                Collections.singletonList(new GenesisAuthority.Builder(GenesisAuthority.DEFAULT_ROLE).build()));
        
        final EscalatedAccessRunner escalatedAccessRunner = EscalatedAccessRunner
                                  .create(authenticationToken,
                                          Collections.singleton(EscalatedDimensionToken.INSTANCE));
        
        Completable.concatArray(
                    readGroupInfos(groupInfosJson)
                        .flatMapCompletable(giJson -> escalatedAccessRunner.runCompletable(() -> {
                            writeRepo.save(groupInfoModelName, giJson, GROUP_INFO_BUSINESS_KEY);
                            return Completable.complete();
                        }).doOnComplete(() -> indexer.updateIndex((GroupInfo) ModelInstanceFactory.createInstance(giJson)))))
            .blockingAwait();
        
        logger.info("~~~ GroupInfo deploying finished successfully");
    }

    @Override
    public void undeploy() {

    }

    private Observable<JsonObject> readGroupInfos(Resource[] resources) {
        return readJson(resources).map(jsonElement -> jsonElement.getAsJsonObject());
    }

    private Observable<JsonElement> readJson(Resource[] resources) {
        JsonParser parser = new JsonParser();
        return Observable.fromArray(resources)
                .map(resource -> IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8))
                .flatMap(jsonString -> Observable.fromIterable(parser.parse(jsonString).getAsJsonArray()));
    }
}
