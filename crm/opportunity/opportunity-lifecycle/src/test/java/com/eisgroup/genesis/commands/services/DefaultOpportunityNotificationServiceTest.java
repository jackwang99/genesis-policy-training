/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.commands.services;

import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.Collection;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.modeling.types.Opportunity;
import com.eisgroup.genesis.http.factory.HttpClientFactory;
import com.eisgroup.genesis.test.utils.JsonUtils;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

/**
 * @author Dmitry Andronchik
 */
public class DefaultOpportunityNotificationServiceTest {

    private static final String MODEL_NAME = "Opportunity";
    private static final String OPPORTUNITY_IMAGE = "opportunity.json";
    
    @Mock
    private CloseableHttpClient httpClient;
    @Mock
    private HttpClientFactory httpClientFactory;
    
    private DefaultOpportunityNotificationService opportunityNotificationService;
    
    @Mock
    private CloseableHttpResponse httpResponseOk;
    @Mock
    private CloseableHttpResponse httpResponseBadRequest;    
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        opportunityNotificationService = Mockito.spy(new DefaultOpportunityNotificationService(httpClientFactory));
        
        Mockito.when(httpClientFactory.createSecured()).thenReturn(httpClient);
        
        opportunityNotificationService.setBaseUrl("http://baseUrl");
        opportunityNotificationService.setCreateTaskUrl("/services/tasks");
        opportunityNotificationService.setOpportunityStateChangedProcessKey("com.exigen.activiti.crm.manual.task.automated.opportunity.stateChanged");
        opportunityNotificationService.setOpportunityInColdProcessKey("com.exigen.activiti.crm.manual.task.automated.opportunity.cold");
        
        StatusLine statusLineOk = Mockito.mock(StatusLine.class);
        Mockito.when(statusLineOk.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        Mockito.when(httpResponseOk.getStatusLine()).thenReturn(statusLineOk);
        
        StatusLine statusLineBadRequest = Mockito.mock(StatusLine.class);
        Mockito.when(statusLineBadRequest.getStatusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);
        Mockito.when(httpResponseBadRequest.getStatusLine()).thenReturn(statusLineBadRequest);
    }
    
    @Test
    public void testGetCustomerNumbersWithoutAssociations() {
        
        JsonObject opportunityJson = JsonUtils.load(OPPORTUNITY_IMAGE);
        opportunityJson.remove("associations");
        Opportunity opportunity = (Opportunity) ModelInstanceFactory.createInstance(MODEL_NAME, "1", opportunityJson);
        
        Collection<String> customerNumbers = opportunityNotificationService.getCustomerNumbers(opportunity);
        Assert.assertTrue(customerNumbers.isEmpty());        
        
        opportunity = createOpportunity();
        opportunity.setAssociations(Lists.newArrayList());
        
        customerNumbers = opportunityNotificationService.getCustomerNumbers(opportunity);
        Assert.assertTrue(customerNumbers.isEmpty());
    }
    
    @Test
    public void testGetCustomerNumbers() {
        
        Opportunity opportunity = createOpportunity();
        Collection<String> customerNumbers = opportunityNotificationService.getCustomerNumbers(opportunity);
        
        Assert.assertEquals(2, customerNumbers.size());
        Assert.assertThat(customerNumbers, containsInAnyOrder("C0000001184", "C0000001185"));
    }
    
    @Test
    public void testExecuteSuccess() throws OpportunityNotificationException, ClientProtocolException, IOException {
        
        Opportunity opportunity = createOpportunity();
        
        Mockito.when(httpClient.execute(Mockito.any())).thenReturn(httpResponseOk);
        
        opportunityNotificationService.execute("C0000001184", "com.exigen.activiti.crm.manual.task.automated.opportunity.cold", opportunity);
    }
    
    @Test(expected = OpportunityNotificationException.class)
    public void testExecuteFailure() throws OpportunityNotificationException, ClientProtocolException, IOException {
        
        Opportunity opportunity = createOpportunity();
        
        Mockito.when(httpClient.execute(Mockito.any())).thenReturn(httpResponseBadRequest);
        
        opportunityNotificationService.execute("C0000001185", "com.exigen.activiti.crm.manual.task.automated.opportunity.cold", opportunity);
    }
    
    @Test
    public void testStateChanged() throws OpportunityNotificationException, ClientProtocolException, IOException {
        
        Opportunity opportunity = createOpportunity();
        
        Mockito.when(httpClient.execute(Mockito.any())).thenReturn(httpResponseOk);
        
        opportunityNotificationService.stateChanged(opportunity);
        
        Mockito.verify(opportunityNotificationService, Mockito.times(1)).execute("C0000001184", "com.exigen.activiti.crm.manual.task.automated.opportunity.stateChanged", opportunity);
        Mockito.verify(opportunityNotificationService, Mockito.times(1)).execute("C0000001185", "com.exigen.activiti.crm.manual.task.automated.opportunity.stateChanged", opportunity);        
    }

    @Test
    public void testInCold() throws OpportunityNotificationException, ClientProtocolException, IOException {
        
        Opportunity opportunity = createOpportunity();
        
        Mockito.when(httpClient.execute(Mockito.any())).thenReturn(httpResponseOk);
        
        opportunityNotificationService.inCold(opportunity);
        
        Mockito.verify(opportunityNotificationService, Mockito.times(1)).execute("C0000001184", "com.exigen.activiti.crm.manual.task.automated.opportunity.cold", opportunity);
        Mockito.verify(opportunityNotificationService, Mockito.times(1)).execute("C0000001185", "com.exigen.activiti.crm.manual.task.automated.opportunity.cold", opportunity);        
    }    
    
    private Opportunity createOpportunity() {
        JsonObject opportunityJson = JsonUtils.load(OPPORTUNITY_IMAGE);
        return (Opportunity) ModelInstanceFactory.createInstance(MODEL_NAME, "1", opportunityJson);
    }    
}
