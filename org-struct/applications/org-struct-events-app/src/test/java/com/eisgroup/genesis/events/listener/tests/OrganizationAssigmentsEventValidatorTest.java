/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.events.listener.tests;

import com.eisgroup.genesis.commands.publisher.CommandMessageMetadataFactory;
import com.eisgroup.genesis.commands.streams.CommandEnvelope;
import com.eisgroup.genesis.events.EventPublisher;
import com.eisgroup.genesis.events.OrganizationValidationFailureEvent;
import com.eisgroup.genesis.events.OrganizationValidationSuccessEvent;
import com.eisgroup.genesis.events.config.AllEventsConfig;
import com.eisgroup.genesis.events.listener.OrganizationAssigmentsEventValidator;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.model.lifecycle.Command;
import com.eisgroup.genesis.factory.modeling.types.Organization;
import com.eisgroup.genesis.factory.modeling.types.OrganizationalAssignment;
import com.eisgroup.genesis.factory.modeling.types.OrganizationalPerson;
import com.eisgroup.genesis.factory.modeling.types.PolicyBusinessDimensions;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.factory.modeling.types.TransactionDetails;
import com.eisgroup.genesis.json.link.EntityLink;
import com.eisgroup.genesis.json.link.EntityLinkBuilder;
import com.eisgroup.genesis.json.link.EntityLinkBuilderRegistry;
import com.eisgroup.genesis.json.link.EntityLinkResolver;
import com.eisgroup.genesis.json.link.EntityLinkResolverRegistry;
import com.eisgroup.genesis.json.link.LinkingParams;
import com.eisgroup.genesis.lifecycle.events.CommandExecutedEvent;
import com.eisgroup.genesis.lifecycle.events.StateTransition;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.model.external.ExternalModelRepository;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.orgstruct.repository.api.OrganizationStructureRepository;
import com.eisgroup.genesis.policy.core.lifecycle.commands.PolicyCommands;
import com.eisgroup.genesis.policy.core.lifecycle.commands.QuoteCommands;
import com.eisgroup.genesis.policy.core.lifecycle.event.PolicyIntegrationEnvelope;
import com.eisgroup.genesis.policy.core.model.TransactionType;
import com.eisgroup.genesis.repository.ReadContext;
import com.eisgroup.genesis.streams.MessageMetadata;
import com.eisgroup.genesis.streams.publisher.MessagePublisher;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link OrganizationAssigmentsEventValidator}.
 * 
 * @author adainelis
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({
    EventPublisher.class,
    EntityLinkBuilderRegistry.class,
    EntityLinkResolverRegistry.class,
    ExternalModelRepository.class,
    OrganizationStructureRepository.class,
    ModelResolver.class,
    EntityLinkResolverRegistry.class
 })
public class OrganizationAssigmentsEventValidatorTest {
    
    private static final String POLICY_URI = "geroot://TestPolicy/TestPolicy/policy/1b1cc18f-39f8-4aab-ba0f-a019e56c144a";
    private static final String ORG1_URI = "geroot://Organization/Organization//4f2e46d3-fa1f-42f1-8046-e0d7ad174417";
    private static final String ORG2_URI = "geroot://Organization/Organization//e79dedc6-4b05-4b07-a4c3-af4396daac9b";
    private static final String ORG_PERSON_MODEL_NAME = "OrganizationalPerson";
    
    private OrganizationAssigmentsEventValidator validator;
    
    @Mock
    private MessagePublisher messagePublisher;
    
    @Mock
    private EntityLinkBuilderRegistry entityLinkBuilderRegistry;
    
    @Mock
    private EntityLinkResolverRegistry entityLinkResolverRegistry;
    
    @Mock
    private ExternalModelRepository externalModelRepo;
    
    @Mock
    @SuppressWarnings("rawtypes")
    private ModelRepository modelRepo;
    
    @Mock
    private OrganizationStructureRepository organizationStructureRepository;
    
    @Mock
    private PolicySummary policySummary;
    
    @Mock
    private OrganizationalPerson person;
    
    @Mock
    OrganizationalAssignment assigment1;
    
    @Mock
    OrganizationalAssignment assigment2;
    
    @Before
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        when(externalModelRepo.subscribeAsModelRepository(DomainModel.class)).thenReturn(modelRepo);
        
        mockPolicySummary();
        
        mockLinkBuilderAndRegistry();
        
        EntityLink link1 = new EntityLink<>(com.eisgroup.genesis.factory.modeling.types.immutable.Organization.class, ORG1_URI);
        Organization organization1 = createOrganization(link1, "CD1");
        
        EntityLink link2 = new EntityLink<>(com.eisgroup.genesis.factory.modeling.types.immutable.Organization.class, ORG2_URI);
        Organization organization2 = createOrganization(link2, "CD2");
        
        when(assigment1.getOrganization()).thenReturn(link1);
        when(assigment1.getEffectiveDate()).thenReturn(LocalDate.now());
        when(assigment1.getExpirationDate()).thenReturn(LocalDate.now().plus(1, ChronoUnit.YEARS));
        
        when(assigment2.getOrganization()).thenReturn(link2);
        when(assigment2.getEffectiveDate()).thenReturn(LocalDate.now());
        when(assigment2.getExpirationDate()).thenReturn(LocalDate.now().plus(1, ChronoUnit.YEARS));
        
        when(person.getSecurityIdentity()).thenReturn("testUser");
        when(person.getOrganizationAssignments()).thenReturn(Arrays.asList(assigment1, assigment2));
        when(person.getEffectiveDate()).thenReturn(LocalDate.now());
        when(person.getExpirationDate()).thenReturn(LocalDate.now().plus(1, ChronoUnit.YEARS));
        
        when(organizationStructureRepository.loadOrganizationalPersonByUserName("testUser", ORG_PERSON_MODEL_NAME))
            .thenReturn(Observable.just(person));
        
        EntityLinkResolver<Organization> resolver = Mockito.mock(EntityLinkResolver.class);
        when(resolver.resolve(Mockito.eq(link1), any(ReadContext.class)))
            .thenReturn(Single.just(organization1));
        when(resolver.resolve(Mockito.eq(link2), any(ReadContext.class)))
            .thenReturn(Single.just(organization2));    
        
        when(entityLinkResolverRegistry.getByURIScheme("geroot")).thenReturn(resolver);
        
        validator = new AllEventsConfig().organizationAssigmentEventValidator(entityLinkBuilderRegistry,
                entityLinkResolverRegistry, messagePublisher, externalModelRepo, organizationStructureRepository,
                ORG_PERSON_MODEL_NAME);
    }
    
    @Test
    public void shouldPassValidation() throws Exception {
        Command command = new Command(null, QuoteCommands.ISSUE_REQUEST, null, null, null);
        CommandExecutedEvent message = new CommandExecutedEvent(command, Mockito.mock(StateTransition.class), policySummary);

        when(messagePublisher.publishAsync(any()))
                .thenAnswer(invocation -> {
                    PolicyIntegrationEnvelope arg = invocation.getArgument(0);
                    assertThat(arg.getMessage().getClass().getName(), equalTo(OrganizationValidationSuccessEvent.class.getName()));
                    assertThat(arg.getMessage().getPolicyUri(), equalTo(POLICY_URI));
                    return Completable.complete();
                });

        validator.handle(message).test().assertComplete();
    }
    
    @Test
    public void shouldFailValidationDueToInvalidAgent() {
        when(organizationStructureRepository.loadOrganizationalPersonByUserName("testUser", ORG_PERSON_MODEL_NAME))
        .thenReturn(Observable.empty());
        
        Command command = new Command(null, QuoteCommands.ISSUE_REQUEST, null, null, null);
        CommandExecutedEvent message = new CommandExecutedEvent(command, Mockito.mock(StateTransition.class), policySummary);

        when(messagePublisher.publishAsync(any()))
                .thenAnswer(invocation -> validateEnvelope(invocation, "osae001"));

        validator.handle(message).test().assertComplete();
    }
    
    @Test
    public void shouldFailValidationDueToExpiredAgent() {
        when(person.getEffectiveDate()).thenReturn(LocalDate.now().minus(2, ChronoUnit.YEARS));
        when(person.getExpirationDate()).thenReturn(LocalDate.now().minus(1, ChronoUnit.YEARS));
        
        Command command = new Command(null, QuoteCommands.ISSUE_REQUEST, null, null, null);
        CommandExecutedEvent message = new CommandExecutedEvent(command, Mockito.mock(StateTransition.class), policySummary);

        when(messagePublisher.publishAsync(any()))
                .thenAnswer(invocation -> validateEnvelope(invocation, "osae003"));

        validator.handle(message).test().assertComplete();
    }
    
    @Test
    public void shouldFailValidationDueToInvalidOrganization() {
        PolicyBusinessDimensions policyBusinessDimensions = Mockito.mock(PolicyBusinessDimensions.class);
        when(policyBusinessDimensions.getAgency()).thenReturn("CD3");
        when(policyBusinessDimensions.getSubProducer()).thenReturn("testUser");
        when(policySummary.getBusinessDimensions()).thenReturn(policyBusinessDimensions);
        
        Command command = new Command(null, QuoteCommands.ISSUE_REQUEST, null, null, null);
        CommandExecutedEvent message = new CommandExecutedEvent(command, Mockito.mock(StateTransition.class), policySummary);

        when(messagePublisher.publishAsync(any()))
                .thenAnswer(invocation -> validateEnvelope(invocation, "osae002"));

        validator.handle(message).test().assertComplete();
    }
    
    @Test
    public void shouldFailValidationDueToExpiredOrganization() {
        when(assigment1.getEffectiveDate()).thenReturn(LocalDate.now().minus(2, ChronoUnit.YEARS));
        when(assigment1.getExpirationDate()).thenReturn(LocalDate.now().minus(1, ChronoUnit.YEARS));
        
        Command command = new Command(null, QuoteCommands.ISSUE_REQUEST, null, null, null);
        CommandExecutedEvent message = new CommandExecutedEvent(command, Mockito.mock(StateTransition.class), policySummary);

        when(messagePublisher.publishAsync(any()))
                .thenAnswer(invocation -> validateEnvelope(invocation, "osae004"));

        validator.handle(message).test().assertComplete();
    }
    
    @Test
    public void shouldFailValidationDueToNoBusinessDimensions() {
        when(policySummary.getBusinessDimensions()).thenReturn(null);
        
        Command command = new Command(null, QuoteCommands.ISSUE_REQUEST, null, null, null);
        CommandExecutedEvent message = new CommandExecutedEvent(command, Mockito.mock(StateTransition.class), policySummary);

        when(messagePublisher.publishAsync(any()))
                .thenAnswer(invocation -> validateEnvelope(invocation, "osae006"));

        validator.handle(message).test().assertComplete();
    }

    @Test
    public void shouldFailValidationDueToNoAgent() {
        PolicyBusinessDimensions policyBusinessDimensions = Mockito.mock(PolicyBusinessDimensions.class);
        when(policyBusinessDimensions.getAgency()).thenReturn("CD1");
        when(policyBusinessDimensions.getSubProducer()).thenReturn(null);
        when(policySummary.getBusinessDimensions()).thenReturn(policyBusinessDimensions);
        
        Command command = new Command(null, QuoteCommands.ISSUE_REQUEST, null, null, null);
        CommandExecutedEvent message = new CommandExecutedEvent(command, Mockito.mock(StateTransition.class), policySummary);

        when(messagePublisher.publishAsync(any()))
                .thenAnswer(invocation -> validateEnvelope(invocation, "osae007"));

        validator.handle(message).test().assertComplete();
    }
    
    @Test
    public void shouldFailValidationDueToNoAgency() {
        PolicyBusinessDimensions policyBusinessDimensions = Mockito.mock(PolicyBusinessDimensions.class);
        when(policyBusinessDimensions.getAgency()).thenReturn("");
        when(policyBusinessDimensions.getSubProducer()).thenReturn("testUser");
        when(policySummary.getBusinessDimensions()).thenReturn(policyBusinessDimensions);
        
        Command command = new Command(null, QuoteCommands.ISSUE_REQUEST, null, null, null);
        CommandExecutedEvent message = new CommandExecutedEvent(command, Mockito.mock(StateTransition.class), policySummary);

        when(messagePublisher.publishAsync(any()))
                .thenAnswer(invocation -> validateEnvelope(invocation, "osae008"));

        validator.handle(message).test().assertComplete();
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void shouldNotSupportIfNotIssueRequestCommand() {
        com.eisgroup.genesis.command.Command command = new com.eisgroup.genesis.command.Command(null, PolicyCommands.ENDORSE, null);
        CommandEnvelope envelope = new CommandEnvelope(command, null);
        CommandMessageMetadataFactory factory = new CommandMessageMetadataFactory();
        MessageMetadata messageMetadata = factory.createMetadata(envelope);
        
        Boolean result = validator.supports(messageMetadata);
        assertFalse(result);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void mockLinkBuilderAndRegistry() {
        EntityLinkBuilder mockLinkBuilder = Mockito.mock(EntityLinkBuilder.class);
        when(mockLinkBuilder.createLink(Mockito.eq(policySummary), any(LinkingParams.class))).
            thenReturn(new EntityLink<>(policySummary.getClass(), POLICY_URI));
        when(entityLinkBuilderRegistry.getByType(policySummary.getClass())).thenReturn(mockLinkBuilder);
    }

    private void mockPolicySummary() {
        TransactionDetails transactionDetails = Mockito.mock(TransactionDetails.class);
        when(transactionDetails.getTxType()).thenReturn(TransactionType.BOR_TRANSFER.name());
        when(policySummary.getTransactionDetails()).thenReturn(transactionDetails);
        
        PolicyBusinessDimensions policyBusinessDimensions = Mockito.mock(PolicyBusinessDimensions.class);
        when(policyBusinessDimensions.getAgency()).thenReturn("CD1");
        when(policyBusinessDimensions.getSubProducer()).thenReturn("testUser");
        when(policySummary.getBusinessDimensions()).thenReturn(policyBusinessDimensions);
        
        when(policySummary.getModelName()).thenReturn("TestPolicy");
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Organization createOrganization(EntityLink link, String code) {
        Organization organization = new TestOrganization();
        organization.setParent(link);
        organization.setOrganizationCd(code);
        return organization;
    }

    private Object validateEnvelope(InvocationOnMock invocation, String errorCode) {
        PolicyIntegrationEnvelope envelope = invocation.getArgument(0);
        assertThat(envelope.getMessage().getClass().getName(), equalTo(OrganizationValidationFailureEvent.class.getName()));

        OrganizationValidationFailureEvent event = (OrganizationValidationFailureEvent) envelope.getMessage();
        assertThat(event.getPolicyUri(), equalTo(POLICY_URI));
        assertThat(event.getErrorHolder(), notNullValue());
        assertThat(event.getErrorHolder().getCode(), equalTo(errorCode));
        return Completable.complete();
    }
}