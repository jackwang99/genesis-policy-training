/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.batch.commands.handlers.autoprocessing;

/**
 * The Policy jobs.
 *
 * @author aushakou
 * @since 1.0
 */
public final class PolicyJobs {

    public static final String ENDORSEMENT_INIT_JOB = "endorsementInitJob";
    public static final String ENDORSEMENT_ISSUE_JOB = "endorsementIssueJob";
    public static final String ENDORSEMENT_PROPOSING_JOB = "endorsementProposingJob";
    public static final String ENDORSEMENT_RATE_JOB = "endorsementRateJob";

    public static final String RENEWAL_INIT_JOB = "renewalInitJob";
    public static final String RENEWAL_ISSUE_JOB = "renewalIssueJob";
    public static final String RENEWAL_PROPOSING_JOB = "renewalProposingJob";
    public static final String RENEWAL_RATE_JOB = "renewalRateJob";

    public static final String ARCHIVE_JOB = "archiveJob";
    public static final String POLICY_BOR_TRANSFER_JOB = "policyBORTransferJob";
    public static final String DELETE_ARCHIVING_POLICY_JOB = "deleteArchivedPolicyJob";
    public static final String EXPIRATION_QUOTE_JOB = "expirationQuoteJob";

    public static final String QUOTE_COMMON_JOB = "quoteCommonJob";

    private PolicyJobs() {
    }
}