/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.eis.dto;

/**
 * @author dlevchuk
 */
public class DefaultReference implements Reference {

    private String objectName;
    private String objectNumber;
    private String objectId;

    public DefaultReference(String objectName, String objectNumber, String objectId) {
        this.objectName = objectName;
        this.objectNumber = objectNumber;
        this.objectId = objectId;
    }

    public DefaultReference() {
    }

    @Override
    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    @Override
    public String getObjectNumber() {
        return objectNumber;
    }

    public void setObjectNumber(String objectNumber) {
        this.objectNumber = objectNumber;
    }

    @Override
    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
