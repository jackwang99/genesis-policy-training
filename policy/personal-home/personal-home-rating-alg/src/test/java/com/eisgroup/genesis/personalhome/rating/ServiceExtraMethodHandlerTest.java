/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.personalhome.rating;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.powermock.reflect.Whitebox.getInternalState;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;

import com.eisgroup.rating.converter.Converter;
import com.eisgroup.rating.server.model.DestinationClassResolver;
import com.eisgroup.rating.server.model.Mapper;
import org.junit.Test;

/**
 * {@link PersonalHomeServiceExtraMethodHandler} unit tests.
 *
 * @author zhanchen
 * @since 1.0
 */
public class ServiceExtraMethodHandlerTest {

    private PersonalHomeServiceExtraMethodHandler testee = new PersonalHomeServiceExtraMethodHandler();

    @Test
    public void mapperShouldBeAbleToDealWithDateEvenIfItSetAsTime() throws Exception {
        Mapper mapper = testee.resolveMapper(mock(DestinationClassResolver.class));
        Map<AbstractMap.SimpleEntry<String, String>, Converter> converters = getInternalState(mapper, "converters");

        Converter stringToLocalDateConverter = converters
                .get(new AbstractMap.SimpleEntry<>(String.class.getSimpleName(), LocalDate.class.getSimpleName()));
        assertThat("String to date converter must be able to deal either with date or time",
                stringToLocalDateConverter.convert("1970-05-04T13:54:38.100Z"), equalTo(LocalDate.of(1970, 5, 4)));

        Converter stringToDateConverter = converters
                .get(new AbstractMap.SimpleEntry<>(String.class.getSimpleName(), Date.class.getSimpleName()));
        assertThat("String to date converter must be able to deal either with date or time",
                stringToDateConverter.convert("1970-05-04T13:54:38.100Z"), equalTo(toDate(LocalDate.of(1970, 5, 4))));
    }

    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}