/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.eis.dto;

/**
 * EIS supported party types
 *
 * @author dlevchuk
 */
public enum PartyType {
    PERSON_PARTY("PersonParty"),
    NON_PERSON_PARTY("NonPersonParty"),
    LOCATION_PARTY("LocationParty"),
    VEHICLE_PARTY("VehicleParty");

    private String name;

    private PartyType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
