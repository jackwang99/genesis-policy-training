/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.packaging.offer.listener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.eisgroup.genesis.ModelContext;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.model.personalauto.AutoBLOB;
import com.eisgroup.genesis.factory.model.personalauto.AutoCSLCoverage;
import com.eisgroup.genesis.factory.model.personalauto.AutoLOB;
import com.eisgroup.genesis.factory.model.personalauto.AutoMBICoverage;
import com.eisgroup.genesis.factory.model.personalauto.AutoTransactionDetails;
import com.eisgroup.genesis.factory.model.personalauto.AutoVehicle;
import com.eisgroup.genesis.factory.model.personalauto.MBICoverageUserChoiceContainer;
import com.eisgroup.genesis.factory.model.personalauto.PersonalAutoPolicySummary;
import com.eisgroup.genesis.factory.model.personalauto.impl.PersonalAutoFactory;
import com.eisgroup.genesis.factory.modeling.types.LOBEntity;
import com.eisgroup.genesis.factory.modeling.types.PolicyDetail;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.factory.modeling.types.RootEntity;
import com.eisgroup.genesis.factory.modeling.types.TermDetails;
import com.eisgroup.genesis.factory.repository.key.KeyTraversalUtil;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.policy.core.model.PolicyVariations;
import com.google.common.collect.Lists;

import stubbing.ModelResolverStubber;

/**
 * Abstract test fro Grandfathering
 * @author aspichakou
 * @since 1.0
 */
public abstract class AbstractGfTest {
    
    protected static final String GF_COVERAGE = "GFCoverage";

	private static final String MODEL_NAME = "PersonalAuto";

    @Mock
    protected ModelResolver modelResolver;

    PersonalAutoFactory testProductFactory;

    @Before
    public void setUp() {
        testProductFactory = new PersonalAutoFactory();

        MockitoAnnotations.initMocks(this);
        ModelResolverStubber.stub(modelResolver, MODEL_NAME);
        ModelContext.setCurrentInstance(MODEL_NAME, "1");
    }
    
    protected PolicySummary setupPolicy() {
        PersonalAutoPolicySummary policySummary = testProductFactory.createPersonalAutoPolicySummary(PolicyVariations.POLICY);
        policySummary.setKey(new RootEntityKey(UUID.randomUUID(), 1));
        final AutoTransactionDetails transactionDetails = testProductFactory.createAutoTransactionDetails();
		transactionDetails.setTxEffectiveDate(LocalDateTime.of(2018, 1, 1, 0, 0));
        
		policySummary.setTransactionDetails(transactionDetails);
        
        AutoBLOB autoBlob = testProductFactory.createAutoBLOB();
        ArrayList<LOBEntity> lobs = new ArrayList<>();
        AutoLOB lob = testProductFactory.createAutoLOB();
        lobs.add(lob);
        autoBlob.setLobs(lobs);
        policySummary.setBlob(autoBlob);
        PolicyDetail policyDetail = testProductFactory.createAutoPolicyDetail();
        policyDetail.setDeclineReason("dd");
        TermDetails termDetails = testProductFactory.createAutoTermDetails();

        policySummary.setPolicyDetail(policyDetail);
        policySummary.setTermDetails(termDetails);

        AutoVehicle testVehicle1 = createVehicle1();
        AutoVehicle testVehicle2 = createVehicle2();
        AutoVehicle testVehicle3 = createVehicle3();
        
        lob.setRiskItems(Arrays.asList(testVehicle1, testVehicle2, testVehicle3));        

        KeyTraversalUtil.traverseRoot(((RootEntity)policySummary).toJson(), modelResolver.resolveModel(DomainModel.class)); 
        return policySummary;
    }

    private AutoVehicle createVehicle1() {
        AutoVehicle vehicle = testProductFactory.createAutoVehicle();

        AutoCSLCoverage autoCoverage = testProductFactory.createAutoCSLCoverage();
        autoCoverage.setCode("COV1");
        
        MBICoverageUserChoiceContainer container1 = testProductFactory.createMBICoverageUserChoiceContainer();
        container1.setCode("Container1");
        
        MBICoverageUserChoiceContainer container2 = testProductFactory.createMBICoverageUserChoiceContainer();
        container2.setCode("Container2");
        
        AutoMBICoverage otherGfCoverage = testProductFactory.createAutoMBICoverage();
        otherGfCoverage.setCode(GF_COVERAGE);
        otherGfCoverage.setEffectiveDate(LocalDate.of(2000, 1, 1));
        otherGfCoverage.setExpirationDate(LocalDate.of(2019, 1, 16));
        
        container1.setCoverage(otherGfCoverage);
        
		AutoMBICoverage gfCoverage = testProductFactory.createAutoMBICoverage();
        gfCoverage.setCode(GF_COVERAGE);
        gfCoverage.setExpirationDate(LocalDate.of(2021, 1, 16));
        gfCoverage.setEffectiveDate(LocalDate.of(2000, 1, 1));
        
        container2.setCoverage(gfCoverage);
        
        vehicle.setCoverages(Lists.newArrayList(autoCoverage, container1, container2));

        return vehicle;
    }
    
    private AutoVehicle createVehicle2() {
        AutoVehicle vehicle = testProductFactory.createAutoVehicle();
        
        AutoMBICoverage gfCoverage = testProductFactory.createAutoMBICoverage();       
        gfCoverage.setCode(GF_COVERAGE);
        gfCoverage.setExpirationDate(LocalDate.of(2021, 1, 16));
        gfCoverage.setEffectiveDate(LocalDate.of(2000, 1, 1));
        
        MBICoverageUserChoiceContainer container = testProductFactory.createMBICoverageUserChoiceContainer();
        container.setCode("Container");
        container.setCoverage(gfCoverage);
        
        vehicle.setCoverages(Lists.newArrayList(container));
        return vehicle;
    }
    
    private AutoVehicle createVehicle3() {
        AutoVehicle vehicle = testProductFactory.createAutoVehicle();
        
        AutoMBICoverage gfCoverage = testProductFactory.createAutoMBICoverage();       
        gfCoverage.setCode(GF_COVERAGE);
        gfCoverage.setExpirationDate(LocalDate.of(2017, 1, 16));
        gfCoverage.setEffectiveDate(LocalDate.of(2000, 1, 1));
        
        MBICoverageUserChoiceContainer container = testProductFactory.createMBICoverageUserChoiceContainer();
        container.setCode("Container");
        container.setCoverage(gfCoverage);
        
        vehicle.setCoverages(Lists.newArrayList(container));
        return vehicle;
    }
}
