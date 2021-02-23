/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.eisintegration;

import java.time.LocalDate;
import com.eisgroup.genesis.eis.dto.EISLocation;
import com.eisgroup.genesis.eis.dto.EISNonPerson;
import com.eisgroup.genesis.eis.dto.EISPerson;
import com.eisgroup.genesis.eis.dto.EISParty;
import com.eisgroup.genesis.eis.dto.PartyType;
import com.eisgroup.genesis.events.handlers.EISSuitePartyEventHandler;
import com.eisgroup.genesis.factory.modeling.types.Party;
import com.eisgroup.genesis.json.JsonTypeConverter;
import com.eisgroup.genesis.json.key.BaseKey;
import com.eisgroup.genesis.json.key.EntityKey;
import com.eisgroup.genesis.registry.party.constants.PartyRegistryTypes;
import com.google.gson.JsonObject;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import static junit.framework.TestCase.assertTrue;

/**
 * @author dlevchuk
 */
public class DefaultMapperTest {

    @Test
    public void testPerson() {
        String fn = "Ivan";
        String ln = "Ivanov";
        String salutation = "test salutation";
        String maritalStatus = "single";

        JsonObject person = new JsonObject();
        person.addProperty("firstName", fn);
        person.addProperty("lastName", ln);
        person.addProperty("deceased", false);
        person.addProperty("gender", "M");
        person.addProperty("birthDate", JsonTypeConverter.convert(LocalDate.of(2018, 1, 1)));

        person.addProperty("salutation", salutation);

        person.addProperty("maritalStatus", maritalStatus);
        person.addProperty("suffix", "1");
        person.addProperty("customerNumber", "C0001");

        EISParty party = new DefaultMapper().map(new EISSuitePartyEventHandler.PartyPayload(new PartyTest(person).toJson(), PartyRegistryTypes.PERSON));
        assertTrue(party instanceof EISPerson);

        EISPerson result = (EISPerson) party;

        assertThat(result.getPartyType(), equalTo(PartyType.PERSON_PARTY.getName()));
        assertThat(result.getFirstName(), equalTo(fn));
        assertThat(result.getLastName(), equalTo(ln));
        assertThat(result.getDeceased(), equalTo("false"));
        assertThat(result.getGender(), equalTo("M"));
        assertThat(result.getBirthDate(), equalTo("2018-01-01"));
        assertThat(result.getSalutation(), equalTo(salutation));
        assertThat(result.getMaritalStatus(), equalTo(maritalStatus));
        assertThat(result.getSuffix(), equalTo("1"));
        assertThat(result.getCustomerNumber(), equalTo("C0001"));
    }

    @Test
    public void testLocation() {
        String city = "Minsk";
        String address = "Platonova 20a";
        String state = "CA";
        String zip = "0002";
        String country = "USA";

        JsonObject location = new JsonObject();
        location.addProperty("addressLine1", address);
        location.addProperty("city", city);
        location.addProperty("stateProvinceCd", state);
        location.addProperty("postalCode", zip);
        location.addProperty("countryCd", country);

        EISParty party1 = new DefaultMapper().map(new EISSuitePartyEventHandler.PartyPayload(new PartyTest(location).toJson(), PartyRegistryTypes.LOCATION));
        assertTrue(party1 instanceof EISLocation);

        EISLocation result = (EISLocation) party1;

        assertThat(result.getPartyType(), equalTo(PartyType.LOCATION_PARTY.getName()));
        assertThat(result.getStreet1(), equalTo(address));
        assertThat(result.getCity(), equalTo(city));
        assertThat(result.getState(), equalTo(state));
        assertThat(result.getZip(), equalTo(zip));
        assertThat(result.getCountry(), equalTo(country));
    }

    @Test
    public void testLegalEntity() {
        JsonObject legalEntity = new JsonObject();

        EISParty party = new DefaultMapper().map(new EISSuitePartyEventHandler.PartyPayload(new PartyTest(legalEntity).toJson(), PartyRegistryTypes.LEGAL_ENTITY));
        assertTrue(party instanceof EISNonPerson);
    }

    private class PartyTest implements Party {

        private JsonObject original;

        public PartyTest(JsonObject original) {
            this.original = original;
        }

        @Override
        public void setRegistryTypeId(String partyId) {
            setString("registryTypeId", partyId);
        }

        protected void setString(String propertyName, String string) {
            this.original.addProperty(propertyName, string);
        }

        @Override
        public String getRegistryTypeId() {
            if (!getOriginal().has("registryTypeId")) {
                return null;
            }

            return getString("registryTypeId");
        }

        private String getString(String propertyName) {
            return JsonTypeConverter.convert(String.class, this.original.get(propertyName));
        }

        @Override
        public BaseKey getKey() {
            if(getOriginal().has("_key")) {
                return new EntityKey((JsonObject)getOriginal().get("_key"));
            }
            return null;
        }

        private JsonObject getOriginal() {
            return original;
        }

        @Override
        public JsonObject toJson() {
            return this.original;
        }
    }
}
