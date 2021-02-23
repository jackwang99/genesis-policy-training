package com.eisgroup.genesis.batch.commands.handlers.autoprocessing;

import com.eisgroup.genesis.command.Command;
import com.eisgroup.genesis.factory.modeling.types.JobProcessingStrategy;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.jobs.lifecycle.api.commands.output.SubsequentCommand;
import com.eisgroup.genesis.policy.core.jps.services.JobProcessingStrategyService;
import com.eisgroup.genesis.policy.core.test.Utils;
import com.google.gson.JsonObject;
import io.reactivex.Observable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.reflect.Whitebox;

import java.util.UUID;

import static com.eisgroup.genesis.policy.core.test.Utils.POLICY_PATH;
import static com.eisgroup.genesis.policy.core.test.Utils.PolicyBuilder.aPolicyFrom;
import static com.eisgroup.genesis.policy.core.test.Utils.PolicyBuilder.builder;
import static com.eisgroup.genesis.test.utils.JsonUtils.load;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link AbstractArchivePolicyJob}
 *
 * @author ileanavets
 * @since 10.1
 */
public class AbstractArchivePolicyJobTest {

    private static final UUID POLICY_ROOT_ID_1 = UUID.randomUUID();
    private static final UUID POLICY_ROOT_ID_2 = UUID.randomUUID();
    private static final UUID QUOTE_ROOT_ID = UUID.randomUUID();

    @Spy
    private AbstractArchivePolicyJob testStrategyJob;
    @Mock
    private JobProcessingStrategyService jobProcessingStrategyService;
    @Mock
    private JobProcessingStrategy jobProcessingStrategy;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(testStrategyJob, jobProcessingStrategyService);
        when(jobProcessingStrategyService.getActiveStrategies(any(), any())).thenReturn(Observable.just(jobProcessingStrategy));
        doReturn(Boolean.TRUE).when(testStrategyJob).isQuote(argThat(policy -> QUOTE_ROOT_ID.equals(policy.getKey().getRootId())));
    }

    @Test
    public void shouldFilterPolicyWithSameRootId() {
        PolicySummary policy1_1 = mockPolicy(POLICY_ROOT_ID_1, Integer.valueOf(1));
        PolicySummary policy1_2 = mockPolicy(POLICY_ROOT_ID_1, Integer.valueOf(2));
        PolicySummary policy2_1 = mockPolicy(POLICY_ROOT_ID_2, Integer.valueOf(1));
        PolicySummary policy2_2 = mockPolicy(POLICY_ROOT_ID_2, Integer.valueOf(2));
        PolicySummary quote1 = mockPolicy(QUOTE_ROOT_ID, Integer.valueOf(1));
        PolicySummary quote2 = mockPolicy(QUOTE_ROOT_ID, Integer.valueOf(2));

        when(jobProcessingStrategyService.selectPoliciesByStrategy(eq(jobProcessingStrategy), any()))
                .thenReturn(Observable.just(policy1_1, policy1_2, policy2_1, policy2_2, quote1, quote2));

        doAnswer(invocation -> Observable.just(
                new SubsequentCommand(null,
                        new Command(null, null, ((PolicySummary)invocation.getArgument(1)).toJson()))))
                .when(testStrategyJob).toSubsequentCommand(any(), any());

        testStrategyJob.execute()
                .map(SubsequentCommand::getCommand)
                .map(Command::getData)
                .cast(JsonObject.class)
                .map(Utils::toPolicySummary)
                .test()
                .assertNoErrors()
                .assertComplete()
                .assertValues(policy1_1, policy2_1, quote1, quote2);
    }

    private PolicySummary mockPolicy(UUID rootId, Integer revisionNo) {
        return aPolicyFrom(builder(load(POLICY_PATH))
                .withRootId(rootId)
                .withRevision(revisionNo));
    }

}