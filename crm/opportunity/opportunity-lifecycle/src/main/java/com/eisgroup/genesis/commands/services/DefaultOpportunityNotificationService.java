/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.commands.services;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.http.HttpStatus;

import com.eisgroup.genesis.http.entity.JsonRequestEntity;
import com.eisgroup.genesis.http.factory.HttpClientFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;

import com.eisgroup.genesis.factory.modeling.types.immutable.Opportunity;
import com.eisgroup.genesis.json.JsonTypeConverter;
import com.eisgroup.genesis.json.wrapper.DefaultJsonWrapperFactory;
import com.eisgroup.genesis.json.wrapper.JsonWrapperFactory;
import com.google.common.collect.Lists;


/**
 * Default implementation of {@link OpportunityNotificationService}. Creates tasks in external system (EIS Base). 
 * 
 * @author Dmitry Andronchik
 */
public class DefaultOpportunityNotificationService implements OpportunityNotificationService {
    
    private static final String CUSTOMER_MODEL_TYPE = "Customer";
    
    @Value("${eis_base.baseUrl}")
    protected String baseUrl;

    @Value("${eis_base.createTaskUrl}")
    protected String createTaskUrl;
    
    @Value("${genesis.opportunity.task.entityType}")
    protected String taskEntityType;
    
    @Value("${genesis.opportunity.inCold.processKey}")
    protected String opportunityInColdProcessKey;
    
    @Value("${genesis.opportunity.stateChanged.processKey}")
    protected String opportunityStateChangedProcessKey;    

    protected JsonWrapperFactory wrapperFactory = new DefaultJsonWrapperFactory();    
    
    private final HttpClientFactory httpClientFactory;
    
    public DefaultOpportunityNotificationService(HttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
    }

    @Override
    public void inCold(Opportunity opportunity) throws OpportunityNotificationException {
        
        for(String customerNumber : getCustomerNumbers(opportunity)) {
            execute(customerNumber, opportunityInColdProcessKey, opportunity);
        }
    }
    
    @Override
    public void stateChanged(Opportunity opportunity) throws OpportunityNotificationException {
        
        for(String customerNumber : getCustomerNumbers(opportunity)) {
            execute(customerNumber, opportunityStateChangedProcessKey, opportunity);
        }
    }
    
    protected Collection<String> getCustomerNumbers(Opportunity opportunity) {
        
        if(opportunity.getAssociations() == null) {
            return Lists.newArrayList();
        }
        return opportunity.getAssociations()
                .stream()
                .filter(assoc -> (assoc.getLink() != null) && CUSTOMER_MODEL_TYPE.equals(assoc.getLink().getURI().getHost()))
                .map(assoc -> assoc.getEntityNumber())
                .collect(Collectors.toList());
    }
    
    protected void execute(String custometNumber, String processKey, Opportunity opportunity) throws OpportunityNotificationException {
        
        
        try (CloseableHttpClient httpClient = httpClientFactory.createSecured()) {
              
            HttpPost request = new HttpPost(baseUrl + createTaskUrl);
            request.setHeader("Content-type", "application/json");
            request.setEntity(new JsonRequestEntity(wrapperFactory.unwrap(initCreateTaskRequest(processKey, custometNumber, opportunity))));
  
            CloseableHttpResponse response = httpClient.execute(request);
            try {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new OpportunityNotificationException("Can't create task. " + response.getEntity().getContent());
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            throw new OpportunityNotificationException("Can't create task", e);
        }
    }
    
    protected CreateTaskRequest initCreateTaskRequest(String processKey, String custometNumber, Opportunity opportunity) {
        return new CreateTaskRequest(taskEntityType, processKey, custometNumber,
                JsonTypeConverter.convert(LocalDateTime.now().plusDays(5)), 
                JsonTypeConverter.convert(LocalDateTime.now().plusDays(1)),
                "");        
    }

    protected static class CreateTaskRequest {
        
        private static final String DATE_TYPE = "DATE";
        
        private String entityType;
        private String processKey;
        private String referenceId;
        private String warningTypeDate = DATE_TYPE;
        private String dueTypeDate = DATE_TYPE;
        private String dueDateTime;
        private String warningDateTime;
        private String taskDescription;
 
        public CreateTaskRequest(String entityType, String processKey, String referenceId,
             String dueDateTime, String warningDateTime, String taskDescription) {
             this.entityType = entityType;
             this.processKey = processKey;
             this.referenceId = referenceId;
             this.dueDateTime = dueDateTime;
             this.warningDateTime = warningDateTime;
             this.taskDescription = taskDescription;
        }        
        
        public String getEntityType() {
            return entityType;
        }

        public String getProcessKey() {
            return processKey;
        }

        public String getReferenceId() {
            return referenceId;
        }

        public String getWarningTypeDate() {
            return warningTypeDate;
        }

        public String getDueTypeDate() {
            return dueTypeDate;
        }

        public String getDueDateTime() {
            return dueDateTime;
        }

        public String getWarningDateTime() {
            return warningDateTime;
        }

        public String getTaskDescription() {
            return taskDescription;
        }

    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setCreateTaskUrl(String createTaskUrl) {
        this.createTaskUrl = createTaskUrl;
    }

    public void setTaskEntityType(String taskEntityType) {
        this.taskEntityType = taskEntityType;
    }

    public void setOpportunityStateChangedProcessKey(String opportunityStateChangedProcessKey) {
        this.opportunityStateChangedProcessKey = opportunityStateChangedProcessKey;
    }

    public void setOpportunityInColdProcessKey(String opportunityInColdProcessKey) {
        this.opportunityInColdProcessKey = opportunityInColdProcessKey;
    }
}
