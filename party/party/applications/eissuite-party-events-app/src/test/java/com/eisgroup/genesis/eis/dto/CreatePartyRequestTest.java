/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.eis.dto;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import com.eisgroup.genesis.json.wrapper.DefaultJsonWrapperFactory;
import com.eisgroup.genesis.json.wrapper.JsonWrapperFactory;
import org.junit.Test;

/**
 * @author dlevchuk
 */
public class CreatePartyRequestTest {
    @Test
    public void test() {
        CreatePartyRequest request =  new CreatePartyRequest(null, Arrays.asList(null, null, new EISLocation()).stream().filter(Objects::nonNull).collect(Collectors.toList()));
        JsonWrapperFactory wrapperFactory = new DefaultJsonWrapperFactory();
        System.out.println(wrapperFactory.unwrap(request).toString());


    }
}
