/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.events.listener.propagation;

import com.eisgroup.genesis.command.result.CommandFailure;
import com.eisgroup.genesis.command.result.CommandResult;
import com.eisgroup.genesis.crm.commands.CrmCommands;
import com.eisgroup.genesis.crm.commands.request.CustomerPartialWriteRequest;
import com.eisgroup.genesis.crm.events.listener.CustomerCommandEventHandler;
import com.eisgroup.genesis.events.EventPublisher;
import com.eisgroup.genesis.events.handler.StreamEventHandler;
import com.eisgroup.genesis.exception.ErrorHolder;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.model.types.modeled.ModeledEntity;
import com.eisgroup.genesis.factory.modeling.types.*;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;
import com.eisgroup.genesis.factory.repository.links.FactoryLink;
import com.eisgroup.genesis.json.JsonEntity;
import com.eisgroup.genesis.json.link.EntityLink;
import com.eisgroup.genesis.json.link.EntityLinkResolver;
import com.eisgroup.genesis.json.link.EntityLinkResolverRegistry;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import com.eisgroup.genesis.propagation.event.FinishPropagationEvent;
import com.eisgroup.genesis.propagation.event.PropagationMessageMetadata;
import com.eisgroup.genesis.propagation.event.StartPropagationEvent;
import com.eisgroup.genesis.propagation.event.model.CorrelationItem;
import com.eisgroup.genesis.propagation.event.model.PartyItem;
import com.eisgroup.genesis.propagation.event.model.PropagationStatus;
import com.eisgroup.genesis.repository.ReadContext;
import com.eisgroup.genesis.streams.MessageMetadata;
import com.eisgroup.genesis.util.GsonUtil;
import com.eisgroup.genesis.version.VersionProperties;
import com.google.gson.JsonObject;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * 
 * @author Dmitry Andronchik
 * @since 9.15
 */
public class CemStartPropagationEventHandler implements StreamEventHandler<StartPropagationEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(CemStartPropagationEventHandler.class);
    
    private static final String KEY_FIELD = "_key";
    private static final String TYPE_FIELD = "_type";
    private static final String MODEL_NAME_FIELD = "_modelName";
    private static final String MODEL_TYPE_FIELD = "_modelType";
    private static final String MODEL_VERSION_FIELD = "_modelVersion";
    private static final String STATUS_FIELD = "status";
    private static final String GEOPOSITION_FIELD = "geoposition";
    private static final String GEO_CORD = "GeoCoord";
    
    private final ModelRepository<DomainModel> domainModelRepository = ModelRepositoryFactory.getRepositoryFor(DomainModel.class);
    
    private EntityLinkResolverRegistry linkResolverRegistry;
    private CustomerCommandEventHandler customerCommandEventHandler;
    private EventPublisher eventPublisher;
    
    public CemStartPropagationEventHandler(EntityLinkResolverRegistry linkResolverRegistry, CustomerCommandEventHandler customerCommandEventHandler, EventPublisher eventPublisher) {
        this.linkResolverRegistry = linkResolverRegistry;
        this.customerCommandEventHandler = customerCommandEventHandler;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean supports(MessageMetadata message) {
        return Optional.ofNullable(message)
            .filter(messageMetadata -> messageMetadata.getVersion().equals(VersionProperties.readProjectVersion()))
            .filter(messageMetadata -> messageMetadata instanceof PropagationMessageMetadata)
            .map(PropagationMessageMetadata.class::cast)
            .map(PropagationMessageMetadata::getCorrelationLink)
            .map(link -> new FactoryLink(new EntityLink<RootEntity>(RootEntity.class, link)).getModelName())
            .map(modelName ->domainModelRepository.getAllModels().contains(modelName))
            .orElse(false);
    }
    
    @Override
    public Completable handle(StartPropagationEvent event) {
        return isAcceptable(event)
                .flatMapCompletable(customer -> applyPatry(customer, event.getCorrelationItem().getEntityLink(), event.getPartyItems()));
    }

    private Maybe<Customer> isAcceptable(StartPropagationEvent event) {
        String entityLink = event.getCorrelationItem().getEntityLink();
        return resolveLinks(Collections.singleton(new EntityLink<>(RootEntity.class, entityLink)))
                .firstElement()
                .filter(entity -> entity instanceof Customer)
                .map(Customer.class::cast);
    }
    
    private Completable applyPatry(Customer customer, String entityLink, Collection<PartyItem> partyItems) {
        return Completable.fromAction(() -> {
            partyItems.forEach(partyItem -> {
                Party party = (Party) ModelInstanceFactory.createInstance(partyItem.getData());
                
                if(party instanceof PersonBase) {
                    updateCustomerPerson(customer, (PersonBase) party);
                } else if(party instanceof LocationBase) {
                    updateCustomerLocation(customer, (LocationBase) party);
                } else if(party instanceof LegalEntityBase) {
                    updateCustomerLegalEntity(customer, (LegalEntityBase) party);
                }
            });
            
            sendCustomerWriteCommand(customer, entityLink);
        });
    }
    
    protected void sendCustomerWriteCommand(Customer customer, String entityLink) {
        CommandResult commandResult = customerCommandEventHandler.executeCommand(CrmCommands.WRITE_CUSTOMER, customer.getModelName(),
                new CustomerPartialWriteRequest(customer).toJson()).blockingGet();
        if (commandResult.isFailure()) {
            ErrorHolder errorHolder = ((CommandFailure) commandResult).getData();
            logger.warn("Can't write customer: " + errorHolder);
            fireFinishEvent(entityLink, PropagationStatus.ERROR, errorHolder);
        } else {
            fireFinishEvent(entityLink, PropagationStatus.COMPLETED, null);
        }
    }    
    
    protected void updateCustomerPerson(Customer customer, PersonBase party) {
        if(customer instanceof IndividualCustomerBase) {
            IndividualCustomerBase individualCustomer = (IndividualCustomerBase) customer;
            IndividualDetails details = individualCustomer.getDetails();
            details.setPerson(updatePartyObject(customer, party, details.getPerson()));
        }
    }
    
    protected void updateCustomerLegalEntity(Customer customer, LegalEntityBase party) {
        if(customer instanceof OrganizationCustomerBase) {
            OrganizationCustomerBase organizationCustomer = (OrganizationCustomerBase) customer;
            BusinessDetails details = organizationCustomer.getDetails();
            details.setLegalEntity(updatePartyObject(customer, party, details.getLegalEntity()));
        }
    }
    
    protected void updateCustomerLocation(Customer customer, LocationBase party) {
        if (customer.getCommunicationInfo() != null && customer.getCommunicationInfo().getAddresses() != null) {
            customer.getCommunicationInfo().getAddresses()
                .stream()
                .filter(address -> StringUtils.equals(address.getLocation().getRegistryTypeId(), party.getRegistryTypeId()))
                .forEach(address -> ((CrmAddress) address).setLocation(updatePartyObject(customer, party, ((CrmAddress) address).getLocation())));        
        }
    }
    
    @SuppressWarnings("unchecked")
    protected <E extends Party> E updatePartyObject(Customer customer, E sourceParty, E originalParty) {
        JsonObject newParty = GsonUtil.copy(sourceParty.toJson(), KEY_FIELD, TYPE_FIELD, MODEL_NAME_FIELD, MODEL_TYPE_FIELD, MODEL_VERSION_FIELD, STATUS_FIELD);
        JsonObject originalPartyJson = originalParty.toJson();
        newParty.add(KEY_FIELD, originalPartyJson.get(KEY_FIELD));
        newParty.add(TYPE_FIELD, originalPartyJson.get(TYPE_FIELD));
        if(newParty.has(GEOPOSITION_FIELD)) {
            DomainModel domainModel = domainModelRepository.getModel(customer.getModelName(), customer.getModelVersion());
            Optional<ModeledEntity> geoCordType = domainModel.getTypes().values().stream().filter(type -> type.isAssignableTo(GEO_CORD)).findFirst();
            if(geoCordType.isPresent()) {
                newParty.get(GEOPOSITION_FIELD).getAsJsonObject().addProperty(TYPE_FIELD, geoCordType.get().getName());
            }
        }
        return (E) ModelInstanceFactory.createInstance(customer.getModelName(), customer.getModelVersion(), newParty);
    }
    
    @SuppressWarnings("unchecked")
    protected <R extends JsonEntity> Observable<R> resolveLinks(Collection<EntityLink<R>> links) {
        ReadContext opts = new ReadContext.Builder().build();

        return Observable.fromIterable(links)
                .groupBy(link -> link.getURI().getScheme())
                .flatMap(byScheme -> {
                    EntityLinkResolver<R> resolver = linkResolverRegistry.getByURIScheme(byScheme.getKey());
                    return byScheme
                            .toList()
                            .toObservable()
                            .flatMap(schemeLinks -> resolver.resolve(schemeLinks, opts));
                });
    }
    
    protected void fireFinishEvent(String entityLink, PropagationStatus status, ErrorHolder errors) {
        eventPublisher.publishEvent(new FinishPropagationEvent(new CorrelationItem(entityLink), status, errors));
    }    
}
