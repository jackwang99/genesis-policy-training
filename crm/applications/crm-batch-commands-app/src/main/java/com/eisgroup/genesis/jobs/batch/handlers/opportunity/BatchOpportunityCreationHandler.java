/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.jobs.batch.handlers.opportunity;

import com.eisgroup.genesis.criteria.Matcher;
import com.eisgroup.genesis.crm.commands.CrmCommands;
import com.eisgroup.genesis.crm.commands.request.OpportunityWriteRequest;
import com.eisgroup.genesis.facade.config.PagingConfig;
import com.eisgroup.genesis.facade.search.ModeledEntitySearchCriteria;
import com.eisgroup.genesis.facade.search.ModeledEntitySearchRepository;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.eisgroup.genesis.factory.modeling.types.Opportunity;
import com.eisgroup.genesis.factory.modeling.types.OpportunityAssociation;
import com.eisgroup.genesis.factory.modeling.types.builder.OpportunityBuilder;
import com.eisgroup.genesis.factory.modeling.types.immutable.ProductOwned;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;
import com.eisgroup.genesis.factory.repository.ModeledEntitySchemaResolver;
import com.eisgroup.genesis.factory.repository.links.VersionRoot;
import com.eisgroup.genesis.jobs.lifecycle.api.commands.output.SubsequentCommand;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.json.link.EntityLink;
import com.eisgroup.genesis.json.link.EntityLinkBuilderRegistry;
import com.eisgroup.genesis.json.link.LinkingParams;
import com.eisgroup.genesis.model.Variation;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import com.eisgroup.genesis.search.SearchIndexQuery;
import io.reactivex.Observable;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;


/**
 * Command handler that automatically creates opportunity, when the configured number of days left before the end of
 * the existing customers owned products
 *
 * @author dlevchuk
 */
public class BatchOpportunityCreationHandler extends BaseBatchOpportunityCommandHandler {

    public static final String NAME = "batchOpportunityCreation";

    private static final String OPPORTUNITY_MODEL_NAME = "Opportunity";
    private static final String OPPORTUNITY_MODEL_VERSION = "1";
    private static final String INDIVIDUAL_MODEL_NAME = "INDIVIDUALCUSTOMER";
    
    private static final String ENTITY_ASSOCIATION_MODEL_NAME = "EntityAssociation";

    private static final PagingConfig pagingConfig = PagingConfig.getInstance();

    private static final String OPPORTUNITY_DESCRIPTION_TEMPLATE = "Policy Conversion Opportunity: %s Policy# %s with %s %s is approaching its Expiration Date of %s";

    protected ModelRepository<DomainModel> modelRepo = ModelRepositoryFactory.getRepositoryFor(DomainModel.class);

    @Autowired
    protected ModeledEntitySearchRepository searchRepo;
    
    @Autowired
    protected EntityLinkBuilderRegistry builderRegistry;    

    @Value("${genesis.job.crm.opportunityOpenDays}")
    private Integer opportunityOpenDays;

    @Override
    public String getName() {
        return NAME;
    }

    @Nonnull
    @Override
    public Observable<SubsequentCommand> execute() {
        return searchRepo.search(parseCriteria(1, 0))
                .getResultCount()
                .toObservable()
                .flatMap(count -> {
                    Observable<RootEntity> result = Observable.empty();
                    for (int offset = 0; offset < count; offset += pagingConfig.getHardLimit()) {
                        result = result.concatWith(
                                searchRepo.search(parseCriteria(pagingConfig.getHardLimit(), offset)).getResults()
                        );
                    }
                    return result;
                })
                .flatMap(customer -> Observable.fromIterable(((Customer) customer).getProductsOwned())
                        .map(productOwned -> new ImmutablePair<>(customer, productOwned)))
                .map(pair -> new SubsequentCommand(getCommandNameToBeExecuted(),
                                                   new OpportunityWriteRequest(createOpportunity((Customer) pair.getLeft(), (ProductOwned) pair.getRight())),
                                                   Variation.INVARIANT,
                                                   OPPORTUNITY_MODEL_NAME));
    }

    protected ModeledEntitySearchCriteria parseCriteria(Integer limit, Integer offset) {
        DomainModel model = modelRepo.getActiveModel(INDIVIDUAL_MODEL_NAME);
        String searchSchemaName = ModeledEntitySchemaResolver.getSearchSchemaNameUsing(model, Variation.INVARIANT);

        Collection<Matcher> searchMatchers = Collections.singletonList(
                new SearchIndexQuery.FieldMatcher("policyExpirationDate", LocalDate.now().plusDays(opportunityOpenDays).atStartOfDay())
        );

        return new ModeledEntitySearchCriteria(searchSchemaName, searchMatchers, null,
                Collections.singleton("productsOwned"), limit, offset);
    }

    @Override
    protected String getCommandNameToBeExecuted() {
        return CrmCommands.WRITE_OPPORTUNITY;
    }

    public void setOpportunityOpeneDays(Integer opportunityOpenDays) {
        this.opportunityOpenDays = opportunityOpenDays;
    }

    protected Opportunity createOpportunity(Customer customer, ProductOwned productOwned) {
        Opportunity opportunity = OpportunityBuilder
                .createRoot(OPPORTUNITY_MODEL_NAME, OPPORTUNITY_MODEL_VERSION).build();
        RootEntityKey.generateInitialKey(opportunity.getKey());

        opportunity.setDescription(String.format(OPPORTUNITY_DESCRIPTION_TEMPLATE,
                productOwned.getPolicyTypeCd(),
                productOwned.getPolicyNumber(),
                productOwned.getCarrierNameCd(),
                productOwned.getCarrierNameDescription(),
                productOwned.getPolicyExpirationDate()));
        opportunity.setChannel("DIRECT");
        opportunity.setLikelihood("UNKNOWN");
        opportunity.setState("draft");
        
        OpportunityAssociation opportunityAssociation = ModelInstanceFactory.createInstanceByBusinessType(OPPORTUNITY_MODEL_NAME, OPPORTUNITY_MODEL_VERSION, ENTITY_ASSOCIATION_MODEL_NAME);
        opportunityAssociation.setEntityNumber(customer.getCustomerNumber());
        EntityLink entityLink = builderRegistry.getByType(customer.getClass()).createLink(customer, LinkingParams.just(VersionRoot.class));
        opportunityAssociation.setLink(entityLink);
        opportunity.setAssociations(Arrays.asList(opportunityAssociation));

        return opportunity;
    }
}
