/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.tfs.calculation;


import com.eisgroup.genesis.tfs.services.api.TfsPolicyRatingRequest;
import com.eisgroup.genesis.tfs.services.api.TfsPolicyRatingResponse;

/**
 * Interface that will be exposed when deploying OpenL Tfs Rating
 *
 * @author Andrey Arsatyants
 */
public interface TfsPolicyRatingService {

    TfsPolicyRatingResponse calculateTaxes(TfsPolicyRatingRequest request);


}
