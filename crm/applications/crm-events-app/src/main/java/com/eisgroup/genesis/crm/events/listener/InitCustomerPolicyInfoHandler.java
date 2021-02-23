/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.events.listener;

import com.eisgroup.genesis.bam.events.ActivityEventPublisher;
import com.eisgroup.genesis.bam.events.ActivityStreamEventBuilderFactory;
import com.eisgroup.genesis.commands.request.ApplyContainerProductOwnedRequest;
import com.eisgroup.genesis.crm.commands.CrmCommands;
import com.eisgroup.genesis.crm.core.service.RelationshipService;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.eisgroup.genesis.factory.modeling.types.ProductOwned;
import com.eisgroup.genesis.factory.modeling.types.immutable.PolicySummary;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;
import com.eisgroup.genesis.factory.repository.links.FactoryLink;
import com.eisgroup.genesis.factory.repository.links.VersionRoot;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.json.link.EntityLink;
import com.eisgroup.genesis.json.link.EntityLinkBuilder;
import com.eisgroup.genesis.json.link.EntityLinkBuilderRegistry;
import com.eisgroup.genesis.json.link.LinkingParams;
import com.eisgroup.genesis.lifecycle.events.CommandExecutedEvent;
import com.eisgroup.genesis.model.Variation;
import com.eisgroup.genesis.model.external.ExternalModelRepository;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.policy.core.model.PolicyVariations;
import com.google.gson.JsonObject;
import io.reactivex.Completable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Listens to policy NEW_BUSINESS_ISSUE commands. Stores policy related information to customer.
 * 
 * @author Dmitry Andronchik
 * @since 9.13
 */
public class InitCustomerPolicyInfoHandler extends BaseExternalEventHandler {

    @Autowired
    protected ActivityEventPublisher activityEventPublisher;

    @Autowired
    protected ActivityStreamEventBuilderFactory activityStreamEventBuilderFactory;

    @Autowired
    protected RelationshipService relationshipService;

    private static final Logger logger = LoggerFactory.getLogger(InitCustomerPolicyInfoHandler.class);
    
    private static final String MODEL_NAME_FIELD = "_modelName";
    private static final String MODEL_VERSION_FIELD = "_modelVersion";
    private static final String ACTIVITY_MESSAGE = "Policy {0} issued";
    private static final String CUSTOMER_NUMBER = "customerNumber";

    protected final EntityLinkBuilderRegistry entityLinkBuilderRegistry;
    protected final CustomerCommandEventHandler customerCommandEventHandler;
    protected final ModelRepository<DomainModel> modelRepo;
    protected final Map<String, String> customerContainerModelMapping;

    public InitCustomerPolicyInfoHandler(Predicate<String> filterFunction, EntityLinkBuilderRegistry entityLinkBuilderRegistry,
                                            CustomerCommandEventHandler customerCommandEventHandler, ExternalModelRepository externalModelRepo,
                                            Map<String, String> customerContainerModelMapping) {
        super(filterFunction, externalModelRepo);
        this.entityLinkBuilderRegistry = entityLinkBuilderRegistry;
        this.customerCommandEventHandler = customerCommandEventHandler;
        this.modelRepo = externalModelRepo.subscribeAsModelRepository(DomainModel.class);
        this.customerContainerModelMapping = customerContainerModelMapping;
    }

    @Override
    public Completable handle(CommandExecutedEvent message) {       
        return Optional.ofNullable(message.getOutput())
            .filter(entity -> entity instanceof PolicySummary)
            .map(entity -> doHandle((PolicySummary) entity, message))
            .orElse(Completable.complete());
    }

    protected Completable doHandle(PolicySummary policy, CommandExecutedEvent message) {
        
        FactoryLink customerFactoryLink = new FactoryLink(policy.getCustomer()); 
        String customerModelName = customerFactoryLink.getModelName();
        if(!customerContainerModelMapping.containsKey(customerModelName)) {
            logger.warn("Unsupported customer model name: " + customerModelName);
            return Completable.complete();
        }
        String agencyContainerModelName = customerContainerModelMapping.get(customerModelName);
        
        RootEntityKey customerRootId = new RootEntityKey(customerFactoryLink.getRootId(), customerFactoryLink.getRevisionNo());
        DomainModel model = modelRepo.getActiveModel(agencyContainerModelName);

        ProductOwned productOwned = (ProductOwned) ModelInstanceFactory.createInstanceByBusinessType(model.getName(), model.getVersion(), ProductOwned.NAME);
        JsonObject productOwnedJson = productOwned.toJson();
        productOwnedJson.addProperty(MODEL_NAME_FIELD, model.getName());
        productOwnedJson.addProperty(MODEL_VERSION_FIELD, model.getVersion());
        
        productOwned.setPolicyNumber(policy.getPolicyNumber());
        productOwned.setPolicyExpirationDate(policy.getTermDetails().getTermExpirationDate().toLocalDate());
        
        EntityLinkBuilder<RootEntity> builder = entityLinkBuilderRegistry.getByType(policy.getClass());
        EntityLink<RootEntity> rootPolicyLink = builder
                         .createLink((RootEntity) policy, LinkingParams.just(Variation.class, PolicyVariations.QUOTE)
                         .with(VersionRoot.class)
                         .with(DomainModel.class, modelRepo.getActiveModel(policy.getModelName())));            
        productOwned.setLink(rootPolicyLink.getURIString());
        logger.warn(CrmCommands.APPLY_CONTAINER_PRODUCT_OWNED + ": " + customerRootId.getId().toString());

        return customerCommandEventHandler.executeCommand(CrmCommands.APPLY_CONTAINER_PRODUCT_OWNED, customerContainerModelMapping.get(customerModelName),
                new ApplyContainerProductOwnedRequest(customerRootId, policy.getBusinessDimensions().getAgency(), productOwned).toJson())
                .map(commandResult -> relationshipService.resolveLinks(Collections.singletonList(policy.getCustomer()), Collections.singletonList(CUSTOMER_NUMBER))
                        .subscribe(customerJson -> {
                            Customer customer = (Customer) ModelInstanceFactory.createInstance(customerJson);
                            logger.warn(CrmCommands.APPLY_CONTAINER_PRODUCT_OWNED + " is ready: " + customerRootId.getId().toString());
                            activityEventPublisher.publish(activityStreamEventBuilderFactory.createBuilder()
                                    .entityId(customerJson.get(RootEntity.TYPE_ATTRIBUTE).getAsString() + "_" + customer.getKey().getRootId().toString())
                                    .entityNumber(customer.getCustomerNumber())
                                    .messageId(MessageFormat.format(ACTIVITY_MESSAGE, policy.getPolicyNumber()))
                                    .build());
                            logger.warn(MessageFormat.format(ACTIVITY_MESSAGE, policy.getPolicyNumber()));
                        }))
                .toCompletable();
    }
}