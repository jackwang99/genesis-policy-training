/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws. 
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.eisgroup.genesis.individual.report.output;

import com.eisgroup.genesis.factory.modeling.types.immutable.ProductOwned;
import com.eisgroup.genesis.report.ReportOutput.ReportAggregate;
import com.eisgroup.genesis.report.ReportOutput.TableName;

/**
 * Owned products hive table representation 
 * 
 * @author azukovskij
 *
 */
@TableName("ProductsOwned")
public class CrmProductsOwnedOutput extends ReportAggregate {

    private static final long serialVersionUID = 6847827426984129299L;
    
    private String policyTypeCd;

    public CrmProductsOwnedOutput() {
    }
    
    public CrmProductsOwnedOutput(CustomerReportOutput root, ProductOwned productOwned) {
        this.timestamp = root.getTimestamp();
        this.rootId = root.getRootId();
        this.revisionNo = root.getRevisionNo();
        this.parentId = root.getRootId();
        this.id = String.valueOf(productOwned.getKey().getId());

        this.policyTypeCd = productOwned.getPolicyTypeCd();
    }

    public String getPolicyTypeCd() {
        return policyTypeCd;
    }

    public void setPolicyTypeCd(String policyTypeCd) {
        this.policyTypeCd = policyTypeCd;
    }
    
}