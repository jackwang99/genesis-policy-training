package com.eisgroup.genesis.batch.commands.handlers.autoprocessing;

import com.eisgroup.genesis.factory.modeling.types.JobProcessingStrategy;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.policy.core.lifecycle.commands.ArchivedPolicyCommands;
import com.eisgroup.genesis.policy.core.lifecycle.commands.ArchivedQuoteCommands;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeEntityKeyRequest;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeRequest;
import com.eisgroup.genesis.policy.core.lifecycle.commands.request.CompositeRootIdentifierRequest;
import com.eisgroup.genesis.policy.core.model.PolicyVariations;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static com.eisgroup.genesis.policy.core.test.Utils.POLICY_PATH;
import static com.eisgroup.genesis.policy.core.test.Utils.PolicyBuilder.aPolicyFrom;
import static com.eisgroup.genesis.policy.core.test.Utils.PolicyBuilder.builder;
import static com.eisgroup.genesis.policy.core.test.Utils.QUOTE_PATH;
import static com.eisgroup.genesis.test.utils.JsonUtils.load;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link DeleteArchivedPolicyJob}
 *
 * @author ileanavets
 * @since 10.1
 */
public class DeleteArchivedPolicyJobTest {

    @Spy
    private DeleteArchivedPolicyJob deleteArchivedPolicyJob;
    @Mock
    private JobProcessingStrategy jobProcessingStrategy;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCreateRequestForDeletingArchivedQuote() {
        PolicySummary archivedQuote = aPolicyFrom(builder(load(QUOTE_PATH)).withVariation(PolicyVariations.ARCHIVED_QUOTE));
        CompositeRequest request = deleteArchivedPolicyJob.getRequest(jobProcessingStrategy, archivedQuote).orElseThrow();
        assertNotNull(request);
        assertEquals(PolicyVariations.ARCHIVED_QUOTE.getName(), request.getVariation());
        assertEquals(ArchivedQuoteCommands.DELETE_ARCHIVED_QUOTE, request.getCommandName());
        assertEquals(archivedQuote.getKey(), ((CompositeEntityKeyRequest) request).getKey());
    }

    @Test
    public void shouldCreateRequestForDeletingArchivedPolicy() {
        PolicySummary archivedPolicy = aPolicyFrom(builder(load(POLICY_PATH)).withVariation(PolicyVariations.ARCHIVED_POLICY));
        CompositeRequest request = deleteArchivedPolicyJob.getRequest(jobProcessingStrategy, archivedPolicy).orElseThrow();
        assertNotNull(request);
        assertEquals(PolicyVariations.ARCHIVED_POLICY.getName(), request.getVariation());
        assertEquals(ArchivedPolicyCommands.DELETE_ARCHIVED_POLICY, request.getCommandName());
        assertTrue(request instanceof CompositeRootIdentifierRequest);
        assertEquals(archivedPolicy.getKey().getRootId(), ((CompositeRootIdentifierRequest) request).getRootId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailCreateRequestForUnsupportedVariation() {
        PolicySummary policy = aPolicyFrom(builder(load(POLICY_PATH)).withVariation(PolicyVariations.POLICY));
        deleteArchivedPolicyJob.getRequest(jobProcessingStrategy, policy);
    }
}