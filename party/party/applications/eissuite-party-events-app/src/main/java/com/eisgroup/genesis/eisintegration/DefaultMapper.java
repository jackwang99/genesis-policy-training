/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.eisintegration;

import java.util.HashMap;
import java.util.Map;
import com.eisgroup.genesis.eis.dto.DefaultReference;
import com.eisgroup.genesis.eis.dto.EISLocation;
import com.eisgroup.genesis.eis.dto.EISNonPerson;
import com.eisgroup.genesis.eis.dto.EISPerson;
import com.eisgroup.genesis.eis.dto.EISParty;
import com.eisgroup.genesis.eis.dto.PartyType;
import com.eisgroup.genesis.events.handlers.EISSuitePartyEventHandler;
import com.eisgroup.genesis.registry.party.constants.PartyRegistryTypes;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author dlevchuk
 */
public class DefaultMapper implements PartyMapper {

    private static final String CUSTOMER_NAVIGATION_REFERENCE = "com.exigen.ipb.crm.domain.Customer";

    private final Map<String, Mapping> mappings = new HashMap<>();

    public DefaultMapper() {
        mappings.put(PartyRegistryTypes.PERSON, this::mapPerson);
        mappings.put(PartyRegistryTypes.LOCATION, this::mapLocation);
        mappings.put(PartyRegistryTypes.LEGAL_ENTITY, this::mapLegalEntity);
    }

    public EISParty map(EISSuitePartyEventHandler.PartyPayload party) {
        if (party == null || party.getParty() == null) {
            return null;
        }

        Mapping mapping = mappings.get(party.getPartyType());

        if (mapping == null) {
            return null;
        }

        return mapping.map(party.getParty());
    }

    private EISLocation mapLocation(JsonObject location) {
        EISLocation eisLocation  = new EISLocation();

        eisLocation.setPartyType(PartyType.LOCATION_PARTY.getName());
        eisLocation.setStreet1(getSafe(location.get("addressLine1")));
        eisLocation.setCity(getSafe(location.get("city")));
        eisLocation.setState(getSafe(location.get("stateProvinceCd")));
        eisLocation.setZip(getSafe(location.get("postalCode")));
        eisLocation.setCountry(getSafe(location.get("countryCd")));
//        TODO: find addressType "eisLocation.setAddressTypeCd(getSafe(location.get("countryCd")))"
//        TODO: find solicit "eisLocation.setDoNotSolicit(getSafe(location.get("countryCd")))"

        eisLocation.setReferences(mockedReferences());

        return eisLocation;
    }

    private EISPerson mapPerson(JsonObject person) {
        EISPerson eisPerson = new EISPerson();

        eisPerson.setPartyType(PartyType.PERSON_PARTY.getName());
        eisPerson.setFirstName(getSafe(person.get("firstName")));
        eisPerson.setLastName(getSafe(person.get("lastName")));
        // TODO: exist in location in address (Exists in location party)
        //"eisPerson.setNationalId();"
        eisPerson.setDeceased(getSafe(person.get("deceased")));
        eisPerson.setGender(getSafe(person.get("gender")));
        eisPerson.setBirthDate(getSafe(person.get("birthDate")));
        eisPerson.setSalutation(getSafe(person.get("salutation")));
        eisPerson.setMaritalStatus(getSafe(person.get("maritalStatus")));
        eisPerson.setSuffix(getSafe(person.get("suffix")));
        eisPerson.setCustomerNumber(getSafe(person.get("customerNumber")));
        // TODO: value in field 'state'
        //        "eisPerson.setArchived();"
        // TODO: in containers
        //        "eisPerson.setAgency();"

        EISParty.References references = mockedReferences();
        eisPerson.setReferences(references);

        eisPerson.setRelationships(mockedRelationships(references.getEntityReference().getObjectId()));

        return eisPerson;
    }

    private EISNonPerson mapLegalEntity(JsonObject legalEntity) {
        EISNonPerson eisNonPerson = new EISNonPerson();

        eisNonPerson.setPartyType(PartyType.NON_PERSON_PARTY.getName());
        eisNonPerson.setLegalName(getSafe(legalEntity.get("legalName")));
        eisNonPerson.setLegalId(getSafe(legalEntity.get("legalId")));

        EISParty.References references = mockedReferences();
        eisNonPerson.setReferences(references);

        eisNonPerson.setRelationships(mockedRelationships(references.getEntityReference().getObjectId()));

        return eisNonPerson;
    }

    // Draft implementation
    // TODO: remove in future
    private EISParty.Relationship[] mockedRelationships(String relatedObjectId) {
        return new EISParty.Relationship[] {new EISParty.Relationship(relatedObjectId, "name1")};
    }

    // Draft implementation
    // TODO: remove in future
    private EISParty.References mockedReferences() {
        return new EISParty.References(
                new EISParty.EntityReference(
                        new DefaultReference("1", "1", "1")),
                new EISParty.NavigationReference(
                        new DefaultReference(CUSTOMER_NAVIGATION_REFERENCE, "b2", "b3"), "role", "0"),
                new EISParty.BobReference("c3", "c3", "rc3", "gg"));
    }

    private String getSafe(JsonElement element) {
        if (element == null || element.getAsString() == null) {
            return StringUtils.EMPTY;
        }
        return element.getAsString();
    }

    interface Mapping {
        EISParty map(JsonObject party);
    }
}
