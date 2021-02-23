package com.eisgroup.genesis.batch.commands.handlers.autoprocessing;

import com.eisgroup.genesis.factory.modeling.types.JobProcessingStrategy;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.policy.core.lifecycle.commands.PolicyCommands;
import com.eisgroup.genesis.policy.core.lifecycle.commands.QuoteCommands;
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
 * Unit test for {@link ArchivePolicyJob}
 *
 * @author ileanavets
 * @since 10.1
 */
public class ArchivePolicyJobTest {

    @Spy
    private ArchivePolicyJob archivePolicyJob;
    @Mock
    private JobProcessingStrategy jobProcessingStrategy;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCreateRequestForArchiveQuote() {
        PolicySummary archivedQuote = aPolicyFrom(builder(load(QUOTE_PATH)));
        CompositeRequest request = archivePolicyJob.getRequest(jobProcessingStrategy, archivedQuote).orElseThrow();
        assertNotNull(request);
        assertEquals(PolicyVariations.QUOTE.getName(), request.getVariation());
        assertEquals(QuoteCommands.ARCHIVE, request.getCommandName());
        assertTrue(request instanceof CompositeEntityKeyRequest);
        assertEquals(archivedQuote.getKey(), ((CompositeEntityKeyRequest) request).getKey());
    }

    @Test
    public void shouldCreateRequestForArchivePolicy() {
        PolicySummary policy = aPolicyFrom(builder(load(POLICY_PATH)));
        CompositeRequest request = archivePolicyJob.getRequest(jobProcessingStrategy, policy).orElseThrow();
        assertNotNull(request);
        assertEquals(PolicyVariations.POLICY.getName(), request.getVariation());
        assertEquals(PolicyCommands.ARCHIVE, request.getCommandName());
        assertTrue(request instanceof CompositeRootIdentifierRequest);
        assertEquals(policy.getKey().getRootId(), ((CompositeRootIdentifierRequest)request).getRootId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailCreateRequestForUnsupportedVariation() {
        PolicySummary archivedQuote = aPolicyFrom(builder(load(POLICY_PATH)).withVariation(PolicyVariations.ARCHIVED_QUOTE));
        archivePolicyJob.getRequest(jobProcessingStrategy, archivedQuote);
    }
}