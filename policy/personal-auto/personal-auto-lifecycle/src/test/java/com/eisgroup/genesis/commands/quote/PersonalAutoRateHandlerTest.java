/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.commands.quote;

import com.eisgroup.genesis.factory.model.personalauto.PersonalAutoPolicySummary;
import com.eisgroup.genesis.factory.modeling.types.AutoPolicySummary;
import com.eisgroup.genesis.factory.modeling.types.VehicleRiskItem;
import com.eisgroup.genesis.factory.modeling.types.immutable.PersonalAutoLOB;
import com.eisgroup.genesis.policy.core.lifecycle.commands.QuoteCommands;
import com.eisgroup.genesis.policy.core.lifecycle.commands.policy.AbstractHandlerTest;
import com.eisgroup.genesis.policy.core.premiums.api.PremiumsProcessor;
import com.eisgroup.genesis.policy.core.rating.repository.model.RateAggregate;
import com.eisgroup.genesis.policy.core.rating.services.RatingService;
import com.eisgroup.genesis.policy.core.rating.services.visitor.PremiumHolderInfo;
import com.eisgroup.genesis.policy.core.rating.services.visitor.PremiumHolderVisitor;
import com.eisgroup.genesis.policy.pnc.auto.services.visitor.AutoPremiumHolderInfo;
import com.eisgroup.genesis.policy.pnc.auto.services.visitor.AutoPremiumHolderVisitor;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.eisgroup.genesis.test.utils.JsonUtils.load;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
public class PersonalAutoRateHandlerTest extends AbstractHandlerTest {

    private static final String QUOTE_WITH_EXCLUDED_ITEMS_PATH = "data/autoQuoteWithExcluded.json";

    @InjectMocks
    private PersonalAutoRateHandler testObject;

    @Mock
    private PremiumsProcessor premiumsProcessor;
    
    @Mock
    private PremiumHolderVisitor<PremiumHolderInfo> premiumHolderVisitor;
    
    @Mock
    private RatingService<AutoPolicySummary> autoRatingService;

    @Override
    @Before
    public void setUp() {
        initMocks(this);
    }

    @Override
    protected String getName() {
        return QuoteCommands.RATE;
    }

    @Override
    protected String getModelName() {
        return "PersonalAuto";
    }

    @Override
    protected String getQuoteFilePath() {
        return QUOTE_WITH_EXCLUDED_ITEMS_PATH;
    }

    @Override
    protected String getPolicyFilePath() {
        return null;
    }

    @Test
    public void shouldInvokePremiumCalculationServicesWithoutExcludedItems() {

        final List<VehicleRiskItem> filteredRiskItems = new ArrayList<>();

        PersonalAutoPolicySummary testQuote = (PersonalAutoPolicySummary) toPolicySummary(load(getQuoteFilePath()));

        //We have to capture riskItems collection during invocation step, because then risk item collection will be
        //returned to initial state and we will not be able to verify risk actual risk items amount
        doAnswer(invocation -> {
            final Collection<PersonalAutoLOB> lobs = ((PersonalAutoPolicySummary) invocation.getArgument(0)).getBlob().getLobs();
            if (lobs != null) {
                final PersonalAutoLOB lob = lobs.iterator().next();
                Collection<VehicleRiskItem> capturedRiskItems = lob.getRiskItems();
                if(capturedRiskItems.isEmpty()){
                    capturedRiskItems = new ArrayList<>();
                }
                filteredRiskItems.addAll(capturedRiskItems);
            }
            return Observable.just(testQuote);
        }).when(premiumsProcessor).calculate(any());

        TestObserver<PersonalAutoPolicySummary> testResult = testObject.calculatePremiums(testQuote).test();
        //Verify that premiumsProcessor retrieves filtered riskItems list
        Assert.assertEquals(2, filteredRiskItems.size());
        //Verify that quote risk items amount were not changed after method invocation
        testResult.assertValue(quote -> {
            final Collection<PersonalAutoLOB> optionalLobs = quote.getBlob().getLobs();
            if(optionalLobs != null) {
                final PersonalAutoLOB lob = optionalLobs.iterator().next();
                return lob.getRiskItems().size() == 2;
            }
            return false;
        });
    }
    
    @Test
    public void shouldBeZeroForEverybody() {
    	final PersonalAutoPolicySummary testQuote = (PersonalAutoPolicySummary) toPolicySummary(load(getQuoteFilePath()));
    	final List<AutoPremiumHolderInfo> visit = new AutoPremiumHolderVisitor().visit(testQuote, testQuote.getModelName());
    	
    	when(premiumHolderVisitor.visit(any(), any())).thenReturn((List)visit);
    	when(autoRatingService.rate(any())).thenReturn(new ArrayList<>());
    	when(autoRatingService.calculateTaxesAndFees(any())).thenReturn(new ArrayList<>());
    	final List<RateAggregate> values = testObject.calculateRates(testQuote).test().values();    	
    	
		final Map<UUID, PremiumHolderInfo> collect = visit.stream().collect(Collectors.toMap(r->r.getPremiumHolderKey().getId(), r->r));
    	Assert.assertEquals(collect.size(), values.size());
    }

}