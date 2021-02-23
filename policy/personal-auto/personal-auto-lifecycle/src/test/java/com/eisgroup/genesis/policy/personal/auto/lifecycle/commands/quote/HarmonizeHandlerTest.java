package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.eisgroup.genesis.command.Command;
import com.eisgroup.genesis.commands.request.IdentifierRequest;
import com.eisgroup.genesis.comparison.ComparisonService;
import com.eisgroup.genesis.comparison.impl.operation.AddOperation;
import com.eisgroup.genesis.comparison.impl.operation.ChangeOperation;
import com.eisgroup.genesis.comparison.impl.operation.RemoveOperation;
import com.eisgroup.genesis.comparison.path.identifier.IdentifierStrategy;
import com.eisgroup.genesis.comparison.representation.DiffPatchEntity;
import com.eisgroup.genesis.lifecycle.executor.CommandExecutionContext;
import com.eisgroup.genesis.factory.json.VariableEntity;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.policy.core.repository.variation.PolicyReadRepository;
import com.eisgroup.genesis.policy.core.repository.variation.QuoteReadRepository;
import com.eisgroup.genesis.policy.core.versioning.Operation;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.PersonalAutoCommands;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.OfferImpactDiffHolder;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.OfferImpactResolver;
import com.eisgroup.genesis.test.utils.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.OfferImpactDiffHolder.NOT_SPECIFIED_MODEL;

@RunWith(MockitoJUnitRunner.class)
public class HarmonizeHandlerTest {

    private static final String OPERATIONS = "operations";

    @InjectMocks
    private HarmonizeHandler harmonizeHandler;

    @Mock
    private PolicyReadRepository policyReadRepository;

    @Mock
    private QuoteReadRepository quoteReadRepository;

    @Mock
    private ComparisonService comparisonService;

    @Mock
    private IdentifierStrategy<JsonObject> identifierStrategy;

    @Spy
    private Collection<com.eisgroup.genesis.comparison.operation.Operation> operationsComparison = asList(new AddOperation(identifierStrategy), new ChangeOperation(identifierStrategy), new RemoveOperation(identifierStrategy));

    @Mock
    private OfferImpactResolver offerImpactResolver;

    @Mock
    protected ModelResolver modelResolver;

    @Mock
    private CommandExecutionContext executionContext;
    
    @Captor
    private ArgumentCaptor<List<OfferImpactDiffHolder>> offerImpactDiffsCaptor;

    /**
     * Diffs:
     * 1) X1 -> X2     | model
     * 2) completeOffer
     * 3) X2 -> X3     | model
     * 4) completeOffer
     * 5) X3 -> X4     | model
     *    removed 2012 | modelYear
     *    added 2015   | firstRegistrationYear
     * 6) X4 -> X5     | model
     *    added 2013   | modelYear
     *    removed      | firstRegistrationYear
     */
    @Test
    public void shouldHarmonizeOneRevision() {
        JsonObject operationDiffs = JsonUtils.load("data/harmonize/one-revision/diffs.json");
        JsonObject sourceObject = JsonUtils.load("data/harmonize/one-revision/source.json");
        JsonObject targetObject = JsonUtils.load("data/harmonize/one-revision/target.json");
        JsonObject diffPatch = JsonUtils.load("data/harmonize/one-revision/diff-patch.json");

        RootEntityKey key = new RootEntityKey(UUID.randomUUID(), 1);

        PolicySummary quote = mock(PolicySummary.class, withSettings().extraInterfaces(VariableEntity.class));
        when(quote.toJson()).thenReturn(targetObject);
        when(quoteReadRepository.load(eq(key), any())).thenReturn(Maybe.just(quote));

        List<Operation> operations = getOperations(operationDiffs);

        when(policyReadRepository.loadPolicyOperations(eq(key), any(), any())).thenReturn(Observable.fromIterable(operations));
        when(comparisonService.compare(eq(sourceObject), eq(targetObject), any())).thenReturn(Single.just(new DiffPatchEntity(diffPatch)));

        PolicySummary harmonizedQuote = mock(PolicySummary.class);
        when(offerImpactResolver.applyDiffs(any(), eq(quote))).thenReturn(Single.just(harmonizedQuote));

        when(executionContext.getEntitySnapshot()).thenReturn(quote);
        CommandExecutionContext.setCurrentInstance(executionContext);
       
        harmonizeHandler.execute(new IdentifierRequest(key), quote)
                .test()
                .assertValue(context -> context.equals(harmonizedQuote));

        verify(offerImpactResolver).applyDiffs(offerImpactDiffsCaptor.capture(), eq(quote));
        assertThat(offerImpactDiffsCaptor.getValue().stream()
                        .map(OfferImpactDiffHolder::getModelName)
                        .collect(Collectors.toList()),
                contains("TestAutoType", "TestAutoType", NOT_SPECIFIED_MODEL, "TestAutoType"));

        assertThat(offerImpactDiffsCaptor.getValue().stream()
                        .map(OfferImpactDiffHolder::getAttribute)
                        .collect(Collectors.toList()),
                contains("model", "modelYear", "amount", "removeMe"));
    }

    /**
     * Diffs:
     * 1) X1 -> X2      |rev.1 model
     * 2) completeOffer |rev.1
     * 3) X2 -> X3      |rev.1 model
     * 4) createVersion |rev.2 <- rev.1
     * 5) removed 2012  |rev.1 modelYear !!After version creation (should be ignored)!!
     * 6) removed 200   |rev.2 enginePower
     * 7) createVersion |rev.3 <- rev.2
     * 8) X3 -> X4      |rev.3 model
     * 9) added BMW     |rev.3 make
     */
    @Test
    public void shouldHarmonizeSeveralRevisions() {
        JsonObject diffs_1 = JsonUtils.load("data/harmonize/several-revisions/diffs-rev1.json");
        JsonObject diffs_2 = JsonUtils.load("data/harmonize/several-revisions/diffs-rev2.json");
        JsonObject diffs_3 = JsonUtils.load("data/harmonize/several-revisions/diffs-rev3.json");
        JsonObject sourceObject = JsonUtils.load("data/harmonize/several-revisions/source.json");
        JsonObject targetObject = JsonUtils.load("data/harmonize/several-revisions/target.json");
        JsonObject diffPatch = JsonUtils.load("data/harmonize/several-revisions/diff-patch.json");

        RootEntityKey key_3 = new RootEntityKey(UUID.randomUUID(), 3);
        RootEntityKey key_2 = new RootEntityKey(key_3.getRootId(), 2);
        RootEntityKey key_1 = new RootEntityKey(key_3.getRootId(), 1);
        PolicySummary contextQuote = mock(PolicySummary.class);
        when(contextQuote.toJson()).thenReturn(targetObject);

        PolicySummary quote_3 = quoteWithParent(2L);
        PolicySummary quote_2 = quoteWithParent(1L);
        PolicySummary quote_1 = quoteWithParent(null);
        when(quoteReadRepository.load(eq(key_3), any())).thenReturn(Maybe.just(quote_3));
        when(quoteReadRepository.load(eq(key_2), any())).thenReturn(Maybe.just(quote_2));
        when(quoteReadRepository.load(eq(key_1), any())).thenReturn(Maybe.just(quote_1));

        when(policyReadRepository.loadPolicyOperations(eq(key_3), any(), any())).thenReturn(Observable.fromIterable(getOperations(diffs_3)));
        when(policyReadRepository.loadPolicyOperations(eq(key_2), any(), any())).thenReturn(Observable.fromIterable(getOperations(diffs_2)));
        when(policyReadRepository.loadPolicyOperations(eq(key_1), any(), any())).thenReturn(Observable.fromIterable(getOperations(diffs_1)));

        when(comparisonService.compare(eq(sourceObject), eq(targetObject), any())).thenReturn(Single.just(new DiffPatchEntity(diffPatch)));

        PolicySummary harmonizedQuote = mock(PolicySummary.class);
        when(offerImpactResolver.applyDiffs(any(), eq(contextQuote))).thenReturn(Single.just(harmonizedQuote));

        when(executionContext.getEntitySnapshot()).thenReturn(contextQuote);
        CommandExecutionContext.setCurrentInstance(executionContext);

        harmonizeHandler.execute(new IdentifierRequest(key_3), contextQuote)
                .test()
                .assertValue(context -> context.equals(harmonizedQuote));

        verify(offerImpactResolver).applyDiffs(offerImpactDiffsCaptor.capture(), eq(contextQuote));
        assertThat(offerImpactDiffsCaptor.getValue().stream()
                        .map(OfferImpactDiffHolder::getModelName)
                        .collect(Collectors.toList()),
                contains("TestAutoType", "TestAutoType", "TestAutoType"));

        assertThat(offerImpactDiffsCaptor.getValue().stream()
                        .map(OfferImpactDiffHolder::getAttribute)
                        .collect(Collectors.toList()),
                contains("model", "make", "enginePower"));
    }

    private List<Operation> getOperations(JsonObject diffs_1) {
        List<Operation> operations = StreamSupport.stream(diffs_1.get(OPERATIONS).getAsJsonArray().spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .map(Operation::new)
                .collect(toList());
        Collections.shuffle(operations);
        return operations;
    }

    @Test
    public void testGetName() {
        Assert.assertEquals(PersonalAutoCommands.HARMONIZE, harmonizeHandler.getName());
    }

    private PolicySummary quoteWithParent(Long parentRevisionNo) {
        PolicySummary quote = mock(PolicySummary.class, withSettings().extraInterfaces(VariableEntity.class));
        when(quote.getCreatedFromQuoteRev()).thenReturn(parentRevisionNo);
        return quote;
    }
}