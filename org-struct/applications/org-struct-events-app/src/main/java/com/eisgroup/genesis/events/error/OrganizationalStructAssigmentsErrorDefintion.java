/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.events.error;

import com.eisgroup.genesis.exception.BaseErrorDefinition;

/**
 * Error definitions used when error occurs during validation of policy organizational structure assignment events.
 * 
 * @author adainelis
 *
 */
public class OrganizationalStructAssigmentsErrorDefintion extends BaseErrorDefinition {

    /**
     * Thrown when assigned Agent is not an organizational person.
     * 
     * @messageParam 0 Agent user name.
     * 
     */
    public static final OrganizationalStructAssigmentsErrorDefintion AGENT_NOT_ORGANIZATION_PERSON =
            new OrganizationalStructAssigmentsErrorDefintion("osae001", "Provided agent ''{0}'' is not a organizational person.");
    
    /**
     * Thrown when assigned Agent does not belong to provided Agency.
     * 
     * @messageParam 0 Agent user name.
     * @messageParam 1 Agency name.
     * 
     */
    public static final OrganizationalStructAssigmentsErrorDefintion AGENT_NOT_ASSIGNED_TO_ORGANIZAITON =
            new OrganizationalStructAssigmentsErrorDefintion("osae002", "Provided agent ''{0}'' does no belong to organization ''{1}''.");
    
    /**
     * Thrown when assigned Agent is not in effective period.
     * 
     * @messageParam 0 Agent user name.
     * @messageParam 1 Agent effective date.
     * @messageParam 2 Agent expiration date.
     * 
     */
    public static final OrganizationalStructAssigmentsErrorDefintion AGENT_NOT_EFFECTIVE =
            new OrganizationalStructAssigmentsErrorDefintion("osae003", "Provided agent ''{0}'' is not in effective period. Agent effective date: {1}, "
                    + "expiration date: {2}.");
    
    /**
     * Thrown when assigned Agent has no effective organizational assignments.
     * 
     * @messageParam 0 Agent user name.
     * @messageParam 1 Agency name.
     * 
     */
    public static final OrganizationalStructAssigmentsErrorDefintion AGENT_HAS_NO_EFFECTIVE_ORGANIZAITON =
            new OrganizationalStructAssigmentsErrorDefintion("osae004", "Provided agent ''{0}'' has no effective organizational assignments to ''{1}'' agency.");

    /**
     * Thrown when Organization cannot be resolved by provided uri.
     * 
     * @messageParam 0 uri to Organization.
     * 
     */
    public static final OrganizationalStructAssigmentsErrorDefintion ORGANIZATION_CANNOT_BE_RESOLVED =
            new OrganizationalStructAssigmentsErrorDefintion("osae005", "Failed to resolve Organization by provided uri: {0}.");
    
    /**
     * Thrown when PolicySummary has no business dimensions data.
     */
    public static final OrganizationalStructAssigmentsErrorDefintion BUSINESS_DIMENSIONS_ATTRIBUTE_IS_MANDATORY =
            new OrganizationalStructAssigmentsErrorDefintion("osae006", "''businessDimensions'' attribute is mandatory.");
    
    /**
     * Thrown when PolicySummary business dimensions are missing subproducer data.
     */
    public static final OrganizationalStructAssigmentsErrorDefintion AGENT_CANNOT_BE_EMPTY =
            new OrganizationalStructAssigmentsErrorDefintion("osae007", "''businessDimensions.subProducer'' attribute is mandatory.");
    
    /**
     * Thrown when PolicySummary business dimensions are missing agency data.
     */
    public static final OrganizationalStructAssigmentsErrorDefintion AGENCY_CANNOT_BE_EMPTY =
            new OrganizationalStructAssigmentsErrorDefintion("osae008", "''businessDimensions.agency'' attribute is mandatory.");

    private OrganizationalStructAssigmentsErrorDefintion(String code, String message) {
        super(code, message);
    }
}
