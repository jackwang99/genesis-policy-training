/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.modules;

import com.eisgroup.genesis.commands.customer.conversion.ConversionWriteHandler;
import com.eisgroup.genesis.commands.publisher.api.CommandPublisher;
import com.eisgroup.genesis.crm.commands.ConversionCommands;
import com.eisgroup.genesis.crm.core.service.RelationshipService;
import com.eisgroup.genesis.crm.core.service.campaign.CampaignAssociationService;
import com.eisgroup.genesis.crm.core.service.customer.CustomerAccountsService;
import com.eisgroup.genesis.crm.core.service.customer.CustomerCampaignsService;
import com.eisgroup.genesis.crm.core.service.customer.CustomerCommunicationsService;
import com.eisgroup.genesis.crm.core.service.customer.CustomerOpportunitiesService;
import com.eisgroup.genesis.crm.listener.customer.merge.CampaignCustomerMergeCommandExecutorListener;
import com.eisgroup.genesis.crm.listener.customer.merge.CommunicationCustomerMergeCommandExecutorListener;
import com.eisgroup.genesis.crm.listener.customer.merge.CustomerAccountCustomerMergeCommandExecutorListener;
import com.eisgroup.genesis.crm.listener.customer.merge.OpportunityCustomerMergeCommandExecutorListener;
import com.eisgroup.genesis.crm.repository.CrmReadRepository;
import com.eisgroup.genesis.commands.organizationcustomer.OrganizationSplitHandler;
import com.eisgroup.genesis.crm.commands.CrmCommands;
import com.eisgroup.genesis.crm.repository.impl.ModelAwareNumberGenerator;
import com.eisgroup.genesis.generator.EntityNumberGenerator;
import com.eisgroup.genesis.generator.SequenceGenerator;
import com.eisgroup.genesis.lifecycle.CommandHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ComponentScan("com.eisgroup.genesis.commands.listener")
public class OrganizationLifecycleModule extends CustomerLifecycleModule {

    @Override
    public Map<String, CommandHandler<?, ?>> getCommandHandlers() {
        Map<String, CommandHandler<?, ?>> handlers = super.getCommandHandlers();
        handlers.put(ConversionCommands.WRITE, new ConversionWriteHandler());
        handlers.put(CrmCommands.SPLIT_CUSTOMER, new OrganizationSplitHandler());
        return handlers;
    }

    @Override
    public String getModelName() {
        return "ORGANIZATIONCUSTOMER";
    }

    @Bean
    public EntityNumberGenerator customerNumberGenerator(SequenceGenerator sequenceGenerator) {
        return new ModelAwareNumberGenerator(this.getModelName(), "customer_number", "OC", "%010d", sequenceGenerator);
    }

    @Bean
    public CommunicationCustomerMergeCommandExecutorListener communicationCustomerMergeCommandExecutorListener(CustomerCommunicationsService customerCommunicationsService,
                CommandPublisher commandPublisher, CrmReadRepository readRepo) {
        return new CommunicationCustomerMergeCommandExecutorListener("Communication", customerCommunicationsService, commandPublisher, readRepo);
    }
    
    @Bean
    public OpportunityCustomerMergeCommandExecutorListener opportunityCustomerMergeCommandExecutorListener(CustomerOpportunitiesService customerOpportunitiesService,
                CommandPublisher commandPublisher, CrmReadRepository readRepo) {
        return new OpportunityCustomerMergeCommandExecutorListener("Opportunity", customerOpportunitiesService, commandPublisher, readRepo);
    }
    
    @Bean
    public CampaignCustomerMergeCommandExecutorListener campaignCustomerMergeCommandExecutorListener(CustomerCampaignsService customerCampaignsService,
                CampaignAssociationService campaignAssociationService, RelationshipService relationshipService,
                CommandPublisher commandPublisher, CrmReadRepository readRepo) {
        return new CampaignCustomerMergeCommandExecutorListener("CampaignAssociation", customerCampaignsService, campaignAssociationService, relationshipService, commandPublisher, readRepo);
    }
    
    @Bean
    public CustomerAccountCustomerMergeCommandExecutorListener customerAccountCustomerMergeCommandExecutorListener(CustomerAccountsService customerAccountsService, 
            CommandPublisher commandPublisher, CrmReadRepository readRepo) {
        return new CustomerAccountCustomerMergeCommandExecutorListener("CUSTOMERACCOUNT", customerAccountsService, commandPublisher, readRepo);
    }    
}
