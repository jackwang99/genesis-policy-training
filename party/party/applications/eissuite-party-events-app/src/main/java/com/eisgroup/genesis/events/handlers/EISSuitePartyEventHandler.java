/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.events.handlers;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.eisgroup.genesis.eis.dto.CreatePartyRequest;
import com.eisgroup.genesis.eis.dto.DefaultReference;
import com.eisgroup.genesis.eisintegration.EISClient;
import com.eisgroup.genesis.eisintegration.PartyMapper;
import com.eisgroup.genesis.model.external.ExternalModelRepository;
import com.eisgroup.genesis.registry.party.constants.PartyRegistryTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eisgroup.genesis.events.handler.StreamEventHandler;
import com.eisgroup.genesis.factory.json.ModelRootEntity;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.model.lifecycle.Command;
import com.eisgroup.genesis.factory.model.lifecycle.Modifying;
import com.eisgroup.genesis.factory.model.utils.DomainModelTree;
import com.eisgroup.genesis.factory.utils.selector.JsonNode;
import com.eisgroup.genesis.factory.utils.selector.ModelSelectUtil;
import com.eisgroup.genesis.json.JsonEntity;
import com.eisgroup.genesis.lifecycle.events.CommandExecutedEvent;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.util.GsonUtil;
import com.google.gson.JsonObject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Event handler for party integration with EIS suite
 */
public class EISSuitePartyEventHandler implements StreamEventHandler<CommandExecutedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EISSuitePartyEventHandler.class);

    private ModelRepository<DomainModel> domainModelRepository;

    @Autowired
    private ExternalModelRepository externalModelRepository;

    @Autowired
    private PartyMapper mapper;

    @Autowired
    private EISClient eisClient;

    @PostConstruct
    public void initRepositories() {
        domainModelRepository = externalModelRepository.subscribeAsModelRepository(DomainModel.class);
    }

    @Override
    public Completable handle(CommandExecutedEvent event) {
        Command command = event.getCommand();

        if (command.getAnnotationInfo().stream()
                .noneMatch(annotation -> annotation.getAnnotationType().equals(Modifying.class.getName()))) {
            // non persistent command
            LOGGER.debug("Skipping {} handler execution as {} command was not marked as @Modifying", getClass().getName(), command.getName());
            return Completable.complete();
        }

        JsonEntity outputEntity = event.getOutput();

        if (outputEntity == null || !(outputEntity instanceof ModelRootEntity)) {
            LOGGER.debug("Skipping {} handler execution as no entity was available in event", getClass().getName());
            return Completable.complete();
        }

        return doHandle((ModelRootEntity) outputEntity);
    }

    protected Completable doHandle(ModelRootEntity entity) {
        // decorate entity root/parts
        DomainModel domainModel = domainModelRepository.getModel(entity.getModelName(), entity.getModelVersion());

        if (domainModel == null) {
            LOGGER.debug("Skipping {} handler execution as no lifecycle model for name {} is present in repository", getClass().getName(), entity.getModelName());
            return Completable.complete();
        }

        return collect(entity)
                .toList()
                .doOnSuccess(this::processParty)
                .toCompletable();
    }

    private void processParty(List<PartyPayload> items) {
        if (items == null) {
            return;
        }

        eisClient.execute(new CreatePartyRequest(new DefaultReference("genesis", "C001", "0007"), items.stream()
                .map(item -> {
                    LOGGER.info("Processing party item of type {}", item.getPartyType());
                    return mapper.map(item);
                }).filter(Objects::nonNull)
                .filter(eisParty -> !eisParty.getAttributes().isEmpty())
                .collect(Collectors.toList())));
    }

    private Observable<PartyPayload> collect(ModelRootEntity root) {
        DomainModelTree model = DomainModelTree.from(domainModelRepository.getModel(root.getModelName(), root.getModelVersion()));
        return Observable.concat(
                resolve(model, root, PartyRegistryTypes.PERSON),
                resolve(model, root, PartyRegistryTypes.VEHICLE),
                resolve(model, root, PartyRegistryTypes.LOCATION),
                resolve(model, root, PartyRegistryTypes.LEGAL_ENTITY));
    }

    protected Observable<PartyPayload> resolve(DomainModelTree model, ModelRootEntity entity, String registryTypeName) {
        JsonObject entityJson = entity.toJson();
        String entityType = GsonUtil.getAsString(entityJson, JsonEntity.TYPE_ATTRIBUTE);

        return ModelSelectUtil.selectByType(model, entityType, registryTypeName)
                .flatMap(selectNode -> ModelSelectUtil.resolveNode(new JsonNode(selectNode, entityJson, null), null, selectNode)
                        .map(jsonNode -> (JsonObject) jsonNode.getJson()))
                .map(party -> new PartyPayload(party, registryTypeName));
    }

    public static class PartyPayload {

        private final JsonObject party;
        private final String registryTypeName;

        public PartyPayload(JsonObject party, String registryTypeName) {
            this.party = party;
            this.registryTypeName = registryTypeName;
        }

        public JsonObject getParty() {
            return party;
        }

        public String getPartyType() {
            return registryTypeName;
        }
    }
}
