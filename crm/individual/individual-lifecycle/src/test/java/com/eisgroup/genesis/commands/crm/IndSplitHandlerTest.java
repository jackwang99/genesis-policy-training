/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.commands.crm;

import com.eisgroup.genesis.commands.customer.request.CustomerSplitRequest;
import com.eisgroup.genesis.commands.individualcustomer.IndividualSplitHandler;
import com.eisgroup.genesis.commands.services.CrmValidationErrorDefinition;
import com.eisgroup.genesis.commands.services.CrmValidationService;
import com.eisgroup.genesis.commands.services.CustomerContactService;
import com.eisgroup.genesis.crm.commands.CustomerState;
import com.eisgroup.genesis.crm.repository.CrmReadRepository;
import com.eisgroup.genesis.crm.repository.impl.ModelAwareNumberGenerator;
import com.eisgroup.genesis.crm.repository.impl.ModelAwareNumberGeneratorRegistry;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.model.individualcustomer.IndividualCustomer;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.eisgroup.genesis.factory.modeling.types.IndividualCustomerBase;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;
import com.eisgroup.genesis.factory.services.AccessTrackInfoService;
import com.eisgroup.genesis.json.link.*;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import com.eisgroup.genesis.rdf.repository.RelationshipRepository;
import com.eisgroup.genesis.registry.core.uniqueness.api.UniquenessCriteriaProvider;
import com.eisgroup.genesis.repository.TargetEntityNotFoundException;
import com.eisgroup.genesis.test.utils.JsonUtils;
import com.eisgroup.genesis.uniquefield.validator.UniqueFieldsValidator;
import com.google.gson.JsonObject;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.when;

/**
 * @author Valeriy Sizonenko
 * @since 10.4
 */
public class IndSplitHandlerTest {

    private static final String NEW_INDIVIDUAL_CUSTOMER_JSON = "newIndividualCustomer.json";
    private static final String PREV_INDIVIDUAL_CUSTOMER_JSON = "prevIndividualCustomer.json";

    private static Customer customer;

    @Mock
    private EntityLinkResolverRegistry linkResolverRegistry;
    @Mock
    private EntityLinkResolver<RootEntity> entityLinkResolver;
    @Mock
    private EntityLinkBuilderRegistry builderRegistry;
    @Mock
    private EntityLinkBuilder entityLinkBuilder;
    @Mock
    private CustomerValidator customerValidator;
    @Mock
    private RelationshipRepository relationshipRepository;
    @Mock
    private UniqueFieldsValidator uniqueFieldsValidator;
    @Mock
    private CrmValidationService validationService;
    @Mock
    private UniquenessCriteriaProvider uniquenessCriteriaProvider;
    @Mock
    private ModelAwareNumberGeneratorRegistry numberGeneratorRegistry;
    @Mock
    private ModelAwareNumberGenerator numberGenerator;
    @Mock
    private AccessTrackInfoService accessTrackInfoService;
    @Mock
    private CustomerContactService customerContactService;
    @Mock
    private ModelResolver modelResolver;
    @Mock
    private CrmReadRepository readRepo;

    @InjectMocks
    private IndividualSplitHandler handler = new IndividualSplitHandler();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        ModelRepository<DomainModel> repo = ModelRepositoryFactory.getRepositoryFor(DomainModel.class);
        when(modelResolver.resolveModel(DomainModel.class)).thenReturn(repo.getActiveModel(getModelName()));
        when(modelResolver.getModelName()).thenReturn(getModelName());
        when(modelResolver.getModelVersion()).thenReturn("1");

        when(numberGeneratorRegistry.getNumberGenerator(Mockito.anyString())).thenReturn(numberGenerator);
        String customerNumber = RandomStringUtils.randomAlphanumeric(10);
        when(numberGenerator.generate(Mockito.anyString())).thenReturn(customerNumber);
        when(linkResolverRegistry.getByURIScheme(Mockito.any())).thenReturn(entityLinkResolver);
    }

    private String getModelName() {
        return "INDIVIDUALCUSTOMER";
    }

    @Test
    public void load() {
        customer = createCustomer(NEW_INDIVIDUAL_CUSTOMER_JSON);
        UUID expected = customer.getKey().getRootId();
        String customerNumber = customer.getCustomerNumber();

        when(readRepo.loadCurrent(Mockito.eq(getModelName()), Mockito.any(), Mockito.any())).thenReturn(Single.error(new TargetEntityNotFoundException()));

        handler.load(new CustomerSplitRequest(UUID.fromString("111fba11-b11d-1c11-a111-11c11fb111ab"), customer))
                .test()
                .assertNoErrors()
                .assertOf(cust -> {
                    Assert.assertNotEquals("Customer rootId wan't generated", customer.getKey().getRootId(), expected);
                    Assert.assertNotEquals("Customer number wan't generated", customer.getCustomerNumber(), customerNumber);
                });
    }

    //@Test
    public void validateAsync() {
        customer = createCustomer(NEW_INDIVIDUAL_CUSTOMER_JSON);
        Customer prevCustomer = createCustomer(PREV_INDIVIDUAL_CUSTOMER_JSON);

        UUID fromUUID = UUID.fromString("111fba11-b11d-1c11-a111-11c11fb111ab");

        when(validationService.checkAccessToUpdateWithState(Mockito.eq(customer.getKey()), Mockito.eq(getModelName()), Mockito.eq(Arrays.asList(
                CustomerState.invalid.name(),
                CustomerState.archived.name(),
                CustomerState.deleted.name()
        )))).thenReturn(Observable.empty());
        when(validationService.validateEntity(Mockito.eq(customer))).thenReturn(Observable.empty());
        when(customerValidator.validate(Mockito.eq(customer))).thenReturn(Observable.empty());
        when(uniqueFieldsValidator.validate(Mockito.eq(customer))).thenReturn(Observable.empty());

        when(readRepo.loadCurrentVersion(Mockito.eq(getModelName()), Mockito.eq(fromUUID), Mockito.any())).thenReturn(Single.just(customer.toJson()));
        when(customerValidator.compareCustomerUniqueFields(Mockito.eq(customer), Mockito.eq(customer), Mockito.anyCollection())).thenReturn(Observable.just(CrmValidationErrorDefinition.ENTITY_DIFFERENT_FIELDS.builder().build()));
        when(customerValidator.checkIfPolicySummaryExistsForCustomer(Mockito.any())).thenReturn(Observable.empty());
        when(uniquenessCriteriaProvider.getCriteria(Mockito.anyString())).thenReturn(Arrays.asList("firstName", "lastName"));

        handler.validateAsync(new CustomerSplitRequest(fromUUID, customer), customer)
                .test()
                .assertValueCount(1)
                .assertValue(value -> value.equals(CrmValidationErrorDefinition.ENTITY_DIFFERENT_FIELDS.builder().build()));

        when(readRepo.loadCurrentVersion(Mockito.eq(getModelName()), Mockito.eq(fromUUID), Mockito.any())).thenReturn(Single.just(prevCustomer.toJson()));
        when(customerValidator.compareCustomerUniqueFields(Mockito.eq(customer), Mockito.eq(prevCustomer), Mockito.anyCollection())).thenReturn(Observable.empty());

        boolean actual = handler.validateAsync(new CustomerSplitRequest(fromUUID, customer), customer)
                .test()
                .assertNoErrors()
                .values()
                .isEmpty();
        Assert.assertTrue("Customer unique fields are not different", actual);

        when(customerValidator.checkIfPolicySummaryExistsForCustomer(Mockito.any())).thenReturn(Observable.just(CrmValidationErrorDefinition.CUSTOMER_POLICY_LINKS.builder().build()));

        handler.validateAsync(new CustomerSplitRequest(fromUUID, customer), customer)
                .test()
                .assertValueCount(1)
                .assertValue(value -> value.equals(CrmValidationErrorDefinition.CUSTOMER_POLICY_LINKS.builder().build()));
    }

    @Test
    public void execute() {
        Customer customer = createCustomer(NEW_INDIVIDUAL_CUSTOMER_JSON);
        Customer prevCustomer = createCustomer(PREV_INDIVIDUAL_CUSTOMER_JSON);

        UUID fromUUID = UUID.fromString("111fba11-b11d-1c11-a111-11c11fb111ab");
        when(readRepo.loadCurrentVersion(Mockito.eq(getModelName()), Mockito.eq(fromUUID), Mockito.any())).thenReturn(Single.just(prevCustomer.toJson()));

        when(builderRegistry.getByType(Mockito.any())).thenReturn(entityLinkBuilder);
        when(entityLinkBuilder.createLink(Mockito.any())).thenReturn(new EntityLink(IndividualCustomerBase.class, "customerEntityLink"));

        CustomerSplitRequest splitRequest = Mockito.mock(CustomerSplitRequest.class);
        when(splitRequest.getEntity()).thenReturn(customer);
        when(splitRequest.getSplitFromId()).thenReturn(fromUUID);

        handler.execute(splitRequest, customer)
                .test()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue(res -> ((IndividualCustomer) customer).getSplitFrom() != null);

    }

    private Customer createCustomer(String fileName) {
        JsonObject customerJson = JsonUtils.load(fileName);
        return (Customer) ModelInstanceFactory.createInstance(customerJson);
    }
}
