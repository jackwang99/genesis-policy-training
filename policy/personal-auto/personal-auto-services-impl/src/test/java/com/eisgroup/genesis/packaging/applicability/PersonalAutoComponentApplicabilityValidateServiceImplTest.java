/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work
 * under U.S. copyright laws. CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may
 * be copied, distributed, modified, or incorporated into any other media without EIS Group prior
 * written consent.
 */
package com.eisgroup.genesis.packaging.applicability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.eisgroup.genesis.exception.ErrorHolder;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.model.personalauto.AutoBLOB;
import com.eisgroup.genesis.factory.model.personalauto.AutoVehicle;
import com.eisgroup.genesis.factory.model.personalauto.MBICoverageUserChoiceContainer;
import com.eisgroup.genesis.factory.model.personalauto.immutable.AutoMBICoverage;
import com.eisgroup.genesis.factory.model.utils.DomainModelTree;
import com.eisgroup.genesis.factory.model.utils.TreeNode;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.factory.modeling.types.immutable.GFCoverage;
import com.eisgroup.genesis.factory.modeling.types.immutable.PersonalAutoLOB;
import com.eisgroup.genesis.factory.utils.selector.JsonNode;
import com.eisgroup.genesis.packaging.PackagingDelta;
import com.eisgroup.genesis.packaging.PackagingEntity;
import com.eisgroup.genesis.packaging.PackagingGlobalDelta;
import com.eisgroup.genesis.packaging.PackagingGroupContext;
import com.eisgroup.genesis.packaging.PackagingWorkingContext;
import com.eisgroup.genesis.packaging.context.PackageContext;
import com.eisgroup.genesis.packaging.offer.DimensionRootNode;
import com.eisgroup.genesis.packaging.offer.listener.AbstractGfTest;
import com.eisgroup.genesis.packaging.offer.listener.GfCoverageCollector;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link PersonalAutoComponentApplicabilityValidateServiceImpl}
 * 
 * @author aspichakou
 * @since 1.0
 */
public class PersonalAutoComponentApplicabilityValidateServiceImplTest extends AbstractGfTest {
    
    @InjectMocks
    private PersonalAutoComponentApplicabilityValidateServiceImpl<PackageContext> testObject;
    
    @Mock
    private GfCoverageCollector collector;
    
    @Test
    public void shouldFilterGFCoverages() {        
        final PolicySummary policySummary = setupPolicy();

        final PackagingGlobalDelta globalDelta = new PackagingGlobalDelta();
        final HashMap<String, PackagingEntity> deltaRemove = new HashMap<>();
        final PackagingEntity packagingEntity = new PackagingEntity() {
            @Override
            public String getEntityName() {
                return GFCoverage.NAME;
            }

        };
        deltaRemove.put(GFCoverage.NAME, packagingEntity);

        final HashMap<String, PackagingEntity> entities = new HashMap<>();

        final PackagingDelta delta = new PackagingDelta(entities, deltaRemove);
        final PackagingGroupContext context = Mockito.mock(PackagingGroupContext.class);      
        globalDelta.appendDelta(delta, context);

        final AutoBLOB blob = (AutoBLOB) policySummary.getBlob();
        final PersonalAutoLOB lob = (PersonalAutoLOB) blob.getLobs().iterator().next();
        final AutoVehicle autoVehicle = (AutoVehicle) lob.getRiskItems().iterator().next();
        final MBICoverageUserChoiceContainer mbiContainer = autoVehicle.getMBICoverageUserChoiceContainerCoverages().iterator().next();
        final AutoMBICoverage mbiCoverage = mbiContainer.getCoverage();

        final JsonNode mbiContainerNode = Mockito.mock(JsonNode.class);
        when(mbiContainerNode.getJson()).thenReturn(mbiContainer.toJson());
        final JsonNode coverage = mock(JsonNode.class);
        when(coverage.getJson()).thenReturn(mbiCoverage.toJson());

        final DomainModelTree modelTree = DomainModelTree.from(modelResolver.resolveModel(DomainModel.class));
        final TreeNode root = modelTree.getRoot();

        final Map<String, PackagingWorkingContext> workingCtx = new HashMap<>();
        workingCtx.put(AutoMBICoverage.NAME, new PackagingWorkingContext(root, mbiContainerNode, coverage));
        when(context.getWorkingContext()).thenReturn(workingCtx);

        delta.populateContext(context);

        final DimensionRootNode dimensionRoot = mock(DimensionRootNode.class);

        when(collector.getGfEntities(policySummary.toJson(), policySummary.getTransactionDetails().getTxEffectiveDate().toLocalDate())).thenReturn(List.of(new JsonNode(null, mbiCoverage.toJson(), null)));
        // Execute
        final List<ErrorHolder> errors = testObject.prepareError(policySummary, new PackageContext(null, null, dimensionRoot), globalDelta);
        // Check
        Assert.assertEquals(0, errors.size());

    }
}
