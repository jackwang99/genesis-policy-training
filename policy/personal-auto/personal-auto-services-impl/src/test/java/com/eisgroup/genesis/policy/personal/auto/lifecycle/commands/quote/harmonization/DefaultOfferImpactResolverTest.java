package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization;

import com.eisgroup.genesis.comparison.representation.DiffPatchEntity;
import com.eisgroup.genesis.decision.DecisionService;
import com.eisgroup.genesis.decision.RowResult;
import com.eisgroup.genesis.decision.dsl.model.DecisionModel;
import com.eisgroup.genesis.factory.model.RulesModel;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.factory.utils.BusinessModelJsonTraverser;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.observers.processors.ImpactProcessor;
import com.eisgroup.genesis.test.utils.JsonUtils;
import com.google.gson.JsonObject;
import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultOfferImpactResolverTest {

    @InjectMocks
    private DefaultOfferImpactResolver defaultOfferImpactResolver;

    @Mock
    private DecisionService decisionService;
    @Mock
    private ModelResolver modelResolver;
    @Mock
    private List<ImpactProcessor> impactProcessors;
    @Mock
    private DecisionModel decisionModel;
    @Mock
    private BusinessModelJsonTraverser traverser;
    @Mock
    private PolicySummary policySummary;
    @Mock
    private RowResult rowResult;

    @Test
    public void applyDiffs() {
        when(rowResult.get(any())).thenReturn(null);
        when(policySummary.toJson()).thenReturn(new JsonObject());
        when(decisionService.evaluateTable(anyString(), ArgumentMatchers.<RulesModel>any(), any())).thenReturn(Observable.just(rowResult));
        defaultOfferImpactResolver.applyDiffs(offerImpactDiffHolders(), policySummary)
                .test()
                .assertComplete()
                .assertNoErrors();

        verify(traverser, times(1)).traverse(any(), any(), any());

    }

    private List<OfferImpactDiffHolder> offerImpactDiffHolders() {
        final JsonObject diffPatchJson = JsonUtils.load("data/diff-patch.json");
        final DiffPatchEntity diffPatch = new DiffPatchEntity(diffPatchJson);
        return diffPatch.getDiffs().stream().map(diff -> {
            final String[] tokenizedPath = diff.getPath().split("//");
            return new OfferImpactDiffHolder(tokenizedPath[0], tokenizedPath[1], diff);
        }).collect(Collectors.toList());
    }


}
