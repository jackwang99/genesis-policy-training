/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.jobs.batch.handlers;

import com.eisgroup.genesis.criteria.Matcher;
import com.eisgroup.genesis.crm.commands.CrmCommands;
import com.eisgroup.genesis.crm.commands.CustomerState;
import com.eisgroup.genesis.crm.commands.request.AutomaticCommunicationRequest;
import com.eisgroup.genesis.facade.config.PagingConfig;
import com.eisgroup.genesis.facade.search.ModeledEntitySearchCriteria;
import com.eisgroup.genesis.facade.search.ModeledEntitySearchRepository;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.modeling.types.Communication;
import com.eisgroup.genesis.factory.modeling.types.IndividualCustomerBase;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;
import com.eisgroup.genesis.factory.repository.ModeledEntitySchemaResolver;
import com.eisgroup.genesis.factory.repository.links.VersionRoot;
import com.eisgroup.genesis.jobs.lifecycle.api.commands.output.SubsequentCommand;
import com.eisgroup.genesis.jobs.lifecycle.api.handler.CommandCollectingHandler;
import com.eisgroup.genesis.json.link.EntityLink;
import com.eisgroup.genesis.json.link.EntityLinkBuilderRegistry;
import com.eisgroup.genesis.json.link.LinkingParams;
import com.eisgroup.genesis.model.Variation;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import com.eisgroup.genesis.search.ModeledEntitySearchConstants;
import com.eisgroup.genesis.search.SearchIndexQuery;
import com.google.common.collect.Sets;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Dmitry Andronchik
 */
public class BatchBirthdayCommunicationHandler extends CommandCollectingHandler {

    public static final String NAME = "batchBirthdayCommunication";

    private static final Logger LOG = LoggerFactory.getLogger(BatchBirthdayCommunicationHandler.class);

    private static final PagingConfig pagingConfig = PagingConfig.getInstance();

    private static final String INDIVIDUAL_CUSTOMER_MODEL_NAME = "INDIVIDUALCUSTOMER";
    private static final Set<String> STATES_TO_SKIP = Sets.newHashSet("invalid", "deleted");
    private static final String EMAIL_CHANNEL = "Email";
    private static final String STATE = "state";
    private static final String BIRTH_DATE = "birthDate";
    private static final String HAPPY_BIRTHDAY_MESSAGE = "Our whole team is wishing you the happiest of birthdays! We appreciate and value your business";

    private static Set<String> AVAILABLE_STATES = Sets.newHashSet();

    protected ModelRepository<DomainModel> modelRepo = ModelRepositoryFactory.getRepositoryFor(DomainModel.class);

    @Autowired
    protected ModeledEntitySearchRepository searchRepo;

    @Autowired
    private EntityLinkBuilderRegistry builderRegistry;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected Observable<SubsequentCommand> execute() {
        fillCustomerStates();

        DomainModel model = modelRepo.getActiveModel(INDIVIDUAL_CUSTOMER_MODEL_NAME);
        String searchSchemaName = ModeledEntitySchemaResolver.getSearchSchemaNameUsing(model, Variation.INVARIANT);
        Collection<Matcher> searchMatchers = buildMatchers();

        return searchRepo.search(new ModeledEntitySearchCriteria(searchSchemaName, searchMatchers, null, null, 1, 0))
                .getResultCount()
                .toObservable()
                .flatMap(count -> {
                    Observable<RootEntity> result = Observable.empty();
                    for (int offset = 0; offset < count; offset += pagingConfig.getHardLimit()) {
                        result = result.concatWith(
                                searchRepo.search(new ModeledEntitySearchCriteria(searchSchemaName, searchMatchers, null, null, pagingConfig.getHardLimit(), offset))
                                        .getResults());
                    }
                    return result;
                })
                .map(el -> ((IndividualCustomerBase) el))
                .map(this::toCommand);
    }

    protected Collection<Matcher> buildMatchers() {
        return Arrays.asList(
                new SearchIndexQuery.FieldMatcher(BIRTH_DATE, LocalDate.now()),
                new SearchIndexQuery.FieldMatcher(STATE, AVAILABLE_STATES),
                new SearchIndexQuery.FieldMatcher(ModeledEntitySearchConstants.FIELD_MODEL_NAME, INDIVIDUAL_CUSTOMER_MODEL_NAME)
        );
    }

    private void fillCustomerStates() {
        AVAILABLE_STATES.addAll(
                Stream.of(CustomerState.values())
                        .map(Enum::name)
                        .filter(state -> !STATES_TO_SKIP.contains(state))
                        .collect(Collectors.toSet())
        );
    }

    @SuppressWarnings("rawtypes")
    protected SubsequentCommand toCommand(IndividualCustomerBase customer) {
        LOG.info("Birthday for Customer {}", customer.getCustomerNumber());
        EntityLink entityLink = builderRegistry.getByType(customer.getClass()).createLink(customer, LinkingParams.just(VersionRoot.class));
        return new SubsequentCommand(CrmCommands.AUTOMATIC_COMMUNICATION,
                new AutomaticCommunicationRequest(EMAIL_CHANNEL, customer.getCustomerNumber(), entityLink, HAPPY_BIRTHDAY_MESSAGE),
                Variation.INVARIANT,
                Communication.NAME);
    }
}
