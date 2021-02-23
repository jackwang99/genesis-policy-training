/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.conversion.services;

import com.eisgroup.genesis.factory.model.customeraccount.impl.CUSTOMERACCOUNTFactory;
import com.eisgroup.genesis.factory.modeling.types.CustomerAssociation;

public class ConversionCustomerAccountFactoryServiceImpl implements ConversionCustomerAccountFactoryService {

    @Override
    public CustomerAssociation createGenesisCustomerAssociation() {
        return new CUSTOMERACCOUNTFactory().createGenesisCustomerAssociation();
    }
}
