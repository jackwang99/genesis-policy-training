/*
 * Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.crm.services.customer;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import com.eisgroup.genesis.commands.services.CustomerContactService;
import com.eisgroup.genesis.comparison.path.Path;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.json.ModelRootEntity;
import com.eisgroup.genesis.factory.model.individualcustomer.GenesisCrmBusinessEntity;
import com.eisgroup.genesis.factory.model.individualcustomer.impl.IndividualCustomerImpl;
import com.eisgroup.genesis.factory.model.organizationcustomer.impl.OrganizationCustomerImpl;
import com.eisgroup.genesis.factory.modeling.types.BaseContact;
import com.eisgroup.genesis.factory.modeling.types.CrmCommunicationInfo;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import com.eisgroup.genesis.comparison.diff.Diff;
import com.eisgroup.genesis.comparison.impl.operation.AddOperation;
import com.eisgroup.genesis.comparison.impl.operation.ChangeOperation;
import com.eisgroup.genesis.comparison.impl.operation.RemoveOperation;
import com.eisgroup.genesis.comparison.impl.path.JsonElementPath;
import com.eisgroup.genesis.comparison.path.token.IdentifierToken;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.modeling.types.Customer;
import com.google.common.collect.Sets;

/**
 * Changes updatedOn field in Customer's businessEntities
 *
 * @author Valeriy Sizonenko
 * @since 9.15
 */
public class CustomerContactServiceImpl extends CustomerContactService {

    private Set<String> PREFIXES = Sets.newHashSet("divisions", "businessEntities", "employmentDetails");

    public void setCustomerContactsUpdatedDate(Customer customer, Set<UUID> updatedContactsKeys, LocalDateTime updatedDate) {
        super.setCustomerContactsUpdatedDate(customer, updatedContactsKeys, updatedDate);
        if (customer instanceof IndividualCustomerImpl) {
            IndividualCustomerImpl indCust = ((IndividualCustomerImpl) customer);
            setBusinessEntitiesContactsUpdatedDate(indCust, updatedContactsKeys, updatedDate);
            setEmploymentsContactsUpdatedDate(indCust, updatedContactsKeys, updatedDate);
        } else {
            setDivisionsContactsUpdatedDate(((OrganizationCustomerImpl) customer), updatedContactsKeys, updatedDate);
        }
    }

    private void setBusinessEntitiesContactsUpdatedDate(IndividualCustomerImpl customer, Set<UUID> updatedContactsKeys, LocalDateTime updatedDate) {
        if (customer.getBusinessEntities() != null) {
            customer.getBusinessEntities()
                    .stream()
                    .filter(businessEntity -> ((GenesisCrmBusinessEntity) businessEntity).getCommunicationInfo() != null)
                    .map(businessEntity -> ((GenesisCrmBusinessEntity) businessEntity))
                    .forEach(businessEntity -> setContactsUpdatedDate(businessEntity.getCommunicationInfo(), updatedContactsKeys, updatedDate));
        }
    }

    private void setEmploymentsContactsUpdatedDate(IndividualCustomerImpl customer, Set<UUID> updatedContactsKeys, LocalDateTime updatedDate) {
        if (customer.getEmploymentDetails() != null) {
            customer.getEmploymentDetails()
                    .stream()
                    .filter(details -> details.getCommunicationInfo() != null)
                    .forEach(details -> setContactsUpdatedDate(details.getCommunicationInfo(), updatedContactsKeys, updatedDate));
        }
    }

    private void setDivisionsContactsUpdatedDate(OrganizationCustomerImpl customer, Set<UUID> updatedContactsKeys, LocalDateTime updatedDate) {
        if (customer.getDivisions() != null) {
            customer.getDivisions()
                    .stream()
                    .filter(division -> division.getCommunicationInfo() != null)
                    .forEach(division -> setContactsUpdatedDate(division.getCommunicationInfo(), updatedContactsKeys, updatedDate));
        }
    }

    protected Stream<UUID> getUpdatedContactsKeys(Collection<? extends Diff> diffs, String collectionFieldName, DomainModel model) {
        Set<UUID> keys = new HashSet<>();
        diffs.stream()
            .filter(diff -> diff.getPath().startsWith(collectionFieldName.toString()) || StringUtils.startsWithAny(diff.getPath(), PREFIXES.toArray(new String[0])))
            .filter(diff -> StringUtils.equals(ChangeOperation.OPERATION_TYPE, diff.getOperationType())
                    || StringUtils.equals(AddOperation.OPERATION_TYPE, diff.getOperationType())
                    || StringUtils.equals(RemoveOperation.OPERATION_TYPE, diff.getOperationType())
            )
            .forEach(diff -> {

                JsonElementPath jsonPath = new JsonElementPath(diff.getPath(), model);
                Set<UUID> uuids = getAddedEntityKeys(diff, model);
                if (!uuids.isEmpty()) {
                    keys.addAll(uuids);
                } else {
                    keys.add(jsonPath.getPathTokens()
                            .stream()
                            .filter(token -> token instanceof IdentifierToken && ((IdentifierToken) token).getContentType().isAssignableFrom(JsonObject.class))
                            .reduce((a, b) -> b)
                            .map(token -> UUID.fromString(((IdentifierToken) token).getIdentifierRaw()))
                            .orElse(null));
                }
            });
        keys.removeIf(Objects::isNull);
        return keys.stream();
    }

    private Set<UUID> getAddedEntityKeys(Diff diff, DomainModel model) {
        Set<UUID> uuids = new HashSet<>();
        if (StringUtils.equals(AddOperation.OPERATION_TYPE, diff.getOperationType())
                && StringUtils.startsWithAny(diff.getPath(), PREFIXES.toArray(new String[0]))
                && StringUtils.countMatches(diff.getPath(), Path.PATH_DELIMITER) == 1) {
            JsonObject communicationInfo = diff.getTargetValue().getAsJsonObject().getAsJsonObject("communicationInfo");

            if (communicationInfo == null) {
                return uuids;
            }

            communicationInfo.addProperty(ModelRootEntity.MODEL_NAME_ATTR, model.getName());
            communicationInfo.addProperty(ModelRootEntity.MODEL_VERSION_ATTR, model.getVersion());
            CrmCommunicationInfo crmCommunicationInfo = (CrmCommunicationInfo) ModelInstanceFactory.createInstance(communicationInfo);

            uuids.addAll(getBusinessEntityKeys(crmCommunicationInfo.getAddresses()));
            uuids.addAll(getBusinessEntityKeys(crmCommunicationInfo.getChats()));
            uuids.addAll(getBusinessEntityKeys(crmCommunicationInfo.getEmails()));
            uuids.addAll(getBusinessEntityKeys(crmCommunicationInfo.getPhones()));
            uuids.addAll(getBusinessEntityKeys(crmCommunicationInfo.getSocialNets()));
            uuids.addAll(getBusinessEntityKeys(crmCommunicationInfo.getWebAddresses()));
            return uuids;

        } else {
            return uuids;
        }
    }

    private Set<UUID> getBusinessEntityKeys(Collection<? extends BaseContact> contacts) {
        Set<UUID> uuids = new HashSet<>();
        if (contacts == null) {
            return uuids;
        }
        contacts.forEach(contact -> uuids.add(contact.getKey().getId()));
        return uuids;
    }

}
