/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.json.reducer;

import java.util.Collections;
import java.util.Map;

import com.eisgroup.genesis.decision.DecisionService;
import com.eisgroup.genesis.decision.RowResult;
import com.eisgroup.genesis.decision.dimension.DecisionContext;
import com.eisgroup.genesis.decision.dsl.model.DecisionModel;
import com.eisgroup.genesis.json.reducer.ref.model.AttributeTransformerDecisionTable;
import com.eisgroup.genesis.json.reducer.ref.model.TransformationExecutionContext;
import com.eisgroup.genesis.json.reducer.ref.model.apects.AttributeTransformerAliasAspect;
import com.eisgroup.genesis.node.NodeFactory;
import com.eisgroup.genesis.node.json.JsonNode;
import com.eisgroup.genesis.product.specification.general.Attribute;
import com.eisgroup.genesis.product.specification.general.Component;
import com.eisgroup.genesis.product.specification.general.ProductModel;
import com.eisgroup.genesis.product.specification.general.connection.Connection;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.reactivex.Observable;
import org.junit.Test;


import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link EntityReducer}
 *
 * @author ssauchuk
 * @since 10.1
 */
public class EntityReducerTest {

    private static final String COUNTRY_NEW_ATTR_NAME = "new_country";

    private static final String PERSONAL_AUTO_TYPE = "PersonalAutoPolicySummary";

    private static final String COUNTRY_ATTR_NAME = "country";

    public static final String BEHAVIOUR_VALUE = "Test";

    @Test
    public void shouldBeReturnNewMappedAttributeName() {

        DecisionService decisionService = mock(DecisionService.class);
        RowResult rowResult = mock(RowResult.class);
        when(rowResult.get(AttributeTransformerAliasAspect.NAME)).thenReturn(COUNTRY_NEW_ATTR_NAME);
        when(decisionService.evaluateTable(anyString(), any(DecisionModel.class), any(DecisionContext.class))).thenReturn(Observable.fromArray(rowResult));

        EntityReducer<TransformationExecutionContext> entityReducer = EntityReducer.<TransformationExecutionContext>newBuilder()
                .withObjectMatcher((jsonObject, component) -> true)
                .withAttributeTransformator(new AttributeTransformerDecisionTable(decisionService)).build();

        assertNotNull(entityReducer);

        String data = "{'_modelName': 'PersonalAuto','_modelVersion': '1','_timestamp': '2019-01-24T09:00:12.543Z',"
                + "'_type': 'PersonalAutoPolicySummary','_variation': 'policy',  'country': 'US'}";

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();

        JsonObject json = entityReducer
                .reduce(jsonObject, new TransformationExecutionContext(mockProductSpecification(), BEHAVIOUR_VALUE));

        assertNotNull(json);

        assertThat(json.get(JsonNode.NAME).getAsString(), is(EntityReducer.ROOT_NODE_NAME));
        assertThat(json.get(JsonNode.TYPE).getAsString(), is(PERSONAL_AUTO_TYPE));
        assertThat(json.get(JsonNode.BUSINESS_TYPE).getAsString(), is(NodeFactory.UNKNOWN_BUSINESS_TYPE));

        JsonArray children = json.get(JsonNode.CHILDREN).getAsJsonArray();

        assertThat(children.size(), is(1));
        assertThat(children.get(0).getAsJsonObject().get(JsonNode.NAME).getAsString(), is(COUNTRY_NEW_ATTR_NAME));
    }

    private ProductModel mockProductSpecification() {

        Attribute attribute = mock(Attribute.class);
        when(attribute.getType()).thenReturn("string");
        when(attribute.getId()).thenReturn(COUNTRY_ATTR_NAME);
        when(attribute.getBusinessName()).thenReturn(COUNTRY_ATTR_NAME);

        Connection connection = mock(Connection.class);
        when(connection.getCardinality()).thenReturn(Connection.Cardinality.SINGLE);
        doReturn(Collections.singletonList(connection)).when(attribute).getConnections();

        Component component = mock(Component.class);
        when(component.getDesignName()).thenReturn(PERSONAL_AUTO_TYPE);
        doReturn(Collections.singleton(attribute)).when(component).getAttributes();

        ProductModel productModel = mock(ProductModel.class);

        when(productModel.getRoot()).thenReturn(component);
        doReturn(Collections.singletonList(component)).when(productModel).getComponents();

        return productModel;
    }
}
