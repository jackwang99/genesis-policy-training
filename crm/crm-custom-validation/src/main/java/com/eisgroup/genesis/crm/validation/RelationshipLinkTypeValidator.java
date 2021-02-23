/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.eisgroup.genesis.crm.validation;

import com.eisgroup.genesis.factory.repository.links.FactoryLinkBuilder;
import com.eisgroup.genesis.json.link.EntityLink;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Valeriy Sizonenko
 * @since 10.8
 */
public class RelationshipLinkTypeValidator implements ConstraintValidator<RelationshipLinkType, EntityLink> {

    @Override
    public void initialize(RelationshipLinkType relationshipLinkType) {
    }

    @Override
    public boolean isValid(EntityLink link, ConstraintValidatorContext constraintValidatorContext) {
        return link != null ? StringUtils.equals(FactoryLinkBuilder.LINK_ROOT_PROTOCOL, link.getSchema()) : true;
    }
}