/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.eis.dto;

import java.util.Map;

/**
 * @author dlevchuk
 */
public interface EISParty {

    String getPartyType();

    References getReferences();

    Relationship[] getRelationships();

    Map<String, String> getAttributes();

    class References {
        private EntityReference entityReference;
        private NavigationReference navigationReference;
        private BobReference bobReference;

        public References(EntityReference entityReference, NavigationReference navigationReference, BobReference bobReference) {
            this.entityReference = entityReference;
            this.navigationReference = navigationReference;
            this.bobReference = bobReference;
        }

        public References() {
        }

        public EntityReference getEntityReference() {
            return entityReference;
        }

        public void setEntityReference(EntityReference entityReference) {
            this.entityReference = entityReference;
        }

        public NavigationReference getNavigationReference() {
            return navigationReference;
        }

        public void setNavigationReference(NavigationReference navigationReference) {
            this.navigationReference = navigationReference;
        }

        public BobReference getBobReference() {
            return bobReference;
        }

        public void setBobReference(BobReference bobReference) {
            this.bobReference = bobReference;
        }
    }

    class EntityReference implements Reference {

        private String objectName;
        private String objectNumber;
        private String objectId;

        public EntityReference(Reference reference) {
            this.objectName = reference.getObjectName();
            this.objectNumber = reference.getObjectNumber();
            this.objectId = reference.getObjectId();
        }

        public EntityReference(String objectName, String objectNumber, String objectId) {
            this.objectName = objectName;
            this.objectNumber = objectNumber;
            this.objectId = objectId;
        }

        public EntityReference() {
        }

        public void setObjectName(String objectName) {
            this.objectName = objectName;
        }

        public void setObjectNumber(String objectNumber) {
            this.objectNumber = objectNumber;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        @Override
        public String getObjectName() {
            return objectName;
        }

        @Override
        public String getObjectId() {
            return objectId;
        }

        @Override
        public String getObjectNumber() {
            return objectNumber;
        }
    }

    class NavigationReference implements Reference {

        private String objectName;
        private String objectNumber;
        private String objectId;
        private String role;
        private String referenceIndex;

        public NavigationReference(Reference reference, String role, String referenceIndex) {
            this.objectName = reference.getObjectName();
            this.objectNumber = reference.getObjectNumber();
            this.objectId = reference.getObjectId();
            this.role = role;
            this.referenceIndex = referenceIndex;
        }

        public NavigationReference() {
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getReferenceIndex() {
            return referenceIndex;
        }

        public void setReferenceIndex(String referenceIndex) {
            this.referenceIndex = referenceIndex;
        }

        @Override
        public String getObjectName() {
            return objectName;
        }

        @Override
        public String getObjectId() {
            return objectId;
        }

        @Override
        public String getObjectNumber() {
            return objectNumber;
        }

        public void setObjectName(String objectName) {
            this.objectName = objectName;
        }

        public void setObjectNumber(String objectNumber) {
            this.objectNumber = objectNumber;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }
    }

    class BobReference {

        private String objectName;
        private String objectNumber;
        private String producerCode;
        private String subProducerCode;

        public BobReference(String objectName, String objectNumber, String producerCode, String subProducerCode) {
            this.objectName = objectName;
            this.objectNumber = objectNumber;
            this.producerCode = producerCode;
            this.subProducerCode = subProducerCode;
        }

        public BobReference() {
        }

        public String getObjectName() {
            return objectName;
        }

        public void setObjectName(String objectName) {
            this.objectName = objectName;
        }

        public String getObjectNumber() {
            return objectNumber;
        }

        public void setObjectNumber(String objectNumber) {
            this.objectNumber = objectNumber;
        }

        public String getProducerCode() {
            return producerCode;
        }

        public void setProducerCode(String producerCode) {
            this.producerCode = producerCode;
        }

        public String getSubProducerCode() {
            return subProducerCode;
        }

        public void setSubProducerCode(String subProducerCode) {
            this.subProducerCode = subProducerCode;
        }
    }

    class Relationship {
        private String relatedObjectId;
        private String relationshipRole;

        public Relationship(String relatedObjectId, String relationshipRole) {
            this.relatedObjectId = relatedObjectId;
            this.relationshipRole = relationshipRole;
        }

        public Relationship() {
        }

        public String getRelatedObjectId() {
            return relatedObjectId;
        }

        public void setRelatedObjectId(String relatedObjectId) {
            this.relatedObjectId = relatedObjectId;
        }

        public String getRelationshipRole() {
            return relationshipRole;
        }

        public void setRelationshipRole(String relationshipRole) {
            this.relationshipRole = relationshipRole;
        }
    }
}
