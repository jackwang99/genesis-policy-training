/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.commands.services;

import org.apache.commons.lang3.StringUtils;

import com.eisgroup.genesis.factory.modeling.types.immutable.EntityAssociation;
import com.eisgroup.genesis.factory.modeling.types.Opportunity;
import com.eisgroup.genesis.factory.modeling.types.immutable.RootEntity;
import com.eisgroup.genesis.factory.repository.links.FactoryLink;
import com.eisgroup.genesis.factory.repository.links.FactoryLinkBuilder;
import com.eisgroup.genesis.json.link.EntityLink;

/**
 * 
 * @author Dmitry Andronchik
 */
public class DefaultOpportunityAssociationProcessor implements OpportunityAssociationProcessor {

    private static final String POLICY_MODEL_TYPE = "PolicySummary";
    private static final String QUOTE_VARIATION_NAME = "quote";
    private static final String POLICY_VARIATION_NAME = "policy";

    @Override
    public boolean isQuoted(Opportunity opportunity) {
        return isPolicyAssociation(opportunity, QUOTE_VARIATION_NAME);
    }

    @Override
    public boolean isClosed(Opportunity opportunity) {
        return isPolicyAssociation(opportunity, POLICY_VARIATION_NAME);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void updateQuoteLink(Opportunity opportunity) {
        EntityAssociation association = getEntityAssociation(opportunity, QUOTE_VARIATION_NAME);
        if(association != null) {
            EntityLink<RootEntity> el = association.getLink();
            FactoryLink fl = new FactoryLink(el);
            ((com.eisgroup.genesis.factory.modeling.types.EntityAssociation) association)
                .setLink(new EntityLink(el.getTargetType(), EntityLink.generateURI(FactoryLinkBuilder.LINK_ROOT_PROTOCOL, 
                                fl.getTypeName(), fl.getModelName(), 
                                POLICY_VARIATION_NAME, fl.getRootId().toString())));
        }
    }
    
    protected boolean isPolicyAssociation(Opportunity opportunity, String variationName) {
        return getEntityAssociation(opportunity, variationName) != null;
    }
    
    protected EntityAssociation getEntityAssociation(Opportunity opportunity, String variationName) {
        if (opportunity.getAssociations() != null) {
            for (EntityAssociation association : opportunity.getAssociations()) {
                EntityLink<RootEntity> el = association.getLink();

                FactoryLink factoryLink = new FactoryLink(el);
                if (StringUtils.equals(factoryLink.getTypeName(), POLICY_MODEL_TYPE)
                        && StringUtils.equals(factoryLink.getVariation(), variationName)) {
                    return association;
                }
            }
        }
        return null;
    }
}
