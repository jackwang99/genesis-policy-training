/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.batch.commands.handlers.autoprocessing;

import com.eisgroup.genesis.command.Command;
import com.eisgroup.genesis.factory.modeling.types.JobProcessingStrategy;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.jobs.lifecycle.api.commands.output.SubsequentCommand;
import com.eisgroup.genesis.policy.core.jps.model.ProcessedStepType;
import com.eisgroup.genesis.policy.core.jps.model.StrategyType;
import com.eisgroup.genesis.policy.core.jps.services.JobProcessingStrategyService;
import com.eisgroup.genesis.policy.core.jps.test.TestJobStrategyUtils;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeEntityKeyRequest;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeRequest;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.eisgroup.genesis.policy.core.test.Utils.POLICY_PATH;
import static com.eisgroup.genesis.policy.core.test.Utils.PolicyBuilder.aPolicyFrom;
import static com.eisgroup.genesis.policy.core.test.Utils.PolicyBuilder.builder;
import static com.eisgroup.genesis.test.utils.JsonUtils.load;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

/**
 *
 * Unit test for {@link AbstractStrategyJob}
 *
 * @author ileanavets
 * @since 9.12
 */
public class AbstractStrategyJobTest {

    private static final UUID VALID_POLICY_ROOT_ID1 = UUID.randomUUID();
    private static final UUID VALID_POLICY_ROOT_ID2 = UUID.randomUUID();
    private static final UUID INVALID_POLICY_ROOT_ID = UUID.randomUUID();
    private static final String POLICY_MODEL_NAME = "TestProduct";

    @InjectMocks
    private TestStrategyJob testStrategyJob = new TestStrategyJob();
    @Mock
    private JobProcessingStrategyService jobProcessingStrategyService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        JobProcessingStrategy strategy  = TestJobStrategyUtils.loadStrategy(StrategyType.ENDORSEMENT);

        doReturn(Observable.just(strategy))
                .when(jobProcessingStrategyService).getActiveStrategies(testStrategyJob.getStrategyType(), LocalDate.now());

        doReturn(Observable.just(
                aPolicyFrom(builder(load(POLICY_PATH)).withRootId(VALID_POLICY_ROOT_ID1)),
                aPolicyFrom(builder(load(POLICY_PATH)).withRootId(VALID_POLICY_ROOT_ID2)),
                aPolicyFrom(builder(load(POLICY_PATH)).withRootId(INVALID_POLICY_ROOT_ID))))
                .when(jobProcessingStrategyService).selectPoliciesByStrategy(strategy, testStrategyJob.getProcessedStepType());

    }

    @Test
    public void testJobExecution() {
        TestObserver<SubsequentCommand> executeResult = testStrategyJob.execute().test();
        executeResult
                .assertComplete()
                .assertNoErrors()
                .assertValueCount(2);

        SubsequentCommand command = executeResult.values().get(0);
        assertThat(command.getModelName(), is(POLICY_MODEL_NAME));
        assertCreatedCommand(command.getCommand(), VALID_POLICY_ROOT_ID1);

        command = executeResult.values().get(1);
        assertThat(command.getModelName(), is(POLICY_MODEL_NAME));
        assertCreatedCommand(command.getCommand(), VALID_POLICY_ROOT_ID2);
    }

    private void assertCreatedCommand(Command command, UUID rootId) {
        assertEquals("before1", command.getName());
        Optional<CompositeRequest> requestOptional = CompositeRequest.from(command.getData());
        assertTrue(requestOptional.isPresent());
        CompositeRequest request = requestOptional.get();
        List<String> commandChain = Arrays.asList("before1", "before2", "preRequest", "mainRequest", "postRequest", "after1");
        for (String currCommand: commandChain) {
            assertEquals(currCommand, request.getCommandName());
            assertEquals(rootId, ((CompositeEntityKeyRequest) request).getKey().getRootId());
            request = request.nextRequest();
        }
        assertNull(request);
    }

    private static class TestStrategyJob extends AbstractStrategyJob {

        @Override
        public String getName() {
            return "TestStrategyJob";
        }

        @Nonnull
        @Override
        protected StrategyType getStrategyType() {
            return StrategyType.ENDORSEMENT;
        }

        @Nonnull
        @Override
        protected ProcessedStepType getProcessedStepType() {
            return ProcessedStepType.INIT;
        }

        @Override
        protected boolean filterPolicy(@Nonnull JobProcessingStrategy strategy, @Nonnull PolicySummary policySummary) {
            return !INVALID_POLICY_ROOT_ID.equals(policySummary.getKey().getRootId());
        }

        @Override
        protected Optional<CompositeRequest> getPreRequest(JobProcessingStrategy strategy, PolicySummary policySummary) {
            return Optional.of(new CompositeEntityKeyRequest(policySummary.getKey(), "preRequest", "policy"));
        }

        @Override
        protected Optional<CompositeRequest> getPostRequest(JobProcessingStrategy strategy, PolicySummary policySummary) {
            return Optional.of(new CompositeEntityKeyRequest(policySummary.getKey(), "postRequest", "policy"));
        }

        @Nonnull
        @Override
        protected Optional<CompositeRequest> getRequest(JobProcessingStrategy strategy, PolicySummary policySummary) {
            return Optional.of(new CompositeEntityKeyRequest(policySummary.getKey(), "mainRequest", "policy"));
        }

    }

}