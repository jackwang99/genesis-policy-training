/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.product.server.graphql;

import java.util.Set;

import com.eisgroup.genesis.product.specification.general.factory.ProductModelFactoryRegistry;
import com.eisgroup.genesis.product.specification.policy.factory.PolicyModelFactory;
import com.esigroup.genesis.product.server.graphql.resolver.ConnectionInstrumentation;
import com.esigroup.genesis.product.server.graphql.services.BehaviourValueConverter;
import com.esigroup.genesis.product.server.graphql.services.BehaviourValueConverterImpl;
import com.esigroup.genesis.product.server.ref.config.GraphQLSchemaConfig;
import com.esigroup.genesis.product.services.filter.ProductModelFilter;
import com.esigroup.genesis.product.services.ProductModelFilterImpl;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.eisgroup.genesis.product.specification.mock.JavaProductModelRepository;
import com.eisgroup.genesis.product.specification.repo.ProductModelRepository;

import static org.hamcrest.CoreMatchers.*;

public class GraphQLQueryTest {

    private GraphQL graphQL;

    private ProductModelRepository repository = new JavaProductModelRepository();

    private ProductModelFilter filter = new ProductModelFilterImpl(new ProductModelFactoryRegistry(Set.of(new PolicyModelFactory())));

    private BehaviourValueConverter behaviourValueConverter = new BehaviourValueConverterImpl();

    @Before
    public void setUp() {
        graphQL = GraphQL.newGraphQL(
                new GraphQLSchemaConfig().schemaExtension(repository, filter, behaviourValueConverter, null, null, null))
                .instrumentation(new ConnectionInstrumentation())
                .build();
    }

    @Test
    public void shouldReturnSpecificationByExtensionQuery() {
        ExecutionResult executionResult = graphQL.execute("{productByCd(productCd:\"PersonalAuto\"){productCd}}");

        assertNoErrors(executionResult);
        assertDataContent(executionResult, equalTo("{productByCd={productCd=PersonalAuto}}"));
    }

    @Test
    public void shouldReturnAllRiskComponents() {
        ExecutionResult executionResult = graphQL.execute("{policyProducts{riskComponents{id}}}");

        assertNoErrors(executionResult);
        assertDataContent(executionResult, equalTo("{policyProducts=[{riskComponents=[{id=AutoVehicle}]}]}"));
    }

    @Test
    public void shouldFilterComponents() {
        ExecutionResult executionResult = graphQL
                .execute("{policyProducts{components(filter:{type:\"POLICY\",id:\"DGIGPolicy\"}){id}}}");

        assertNoErrors(executionResult);
        assertDataContent(executionResult, equalTo("{policyProducts=[{components=[{id=DGIGPolicy}]}]}"));
    }

    @Test
    public void shouldReturnAllComponents() {
        ExecutionResult executionResult = graphQL.execute("{policyProducts{components{id}}}");

        assertNoErrors(executionResult);
        assertDataContent(executionResult, containsString("AutoVehicle"));
    }

    /**
     * If filter of rules is not specified should be returned all rules, with entry point and without.
     */
    @Test
    public void shouldReturnAllRules() {

        ExecutionResult executionResult = graphQL.execute("{policyProducts{rules{name}}}");

        assertNoErrors(executionResult);
        assertDataContent(executionResult,
                containsString(JavaProductModelRepository.RULE_WITHOUT_ENTRY_POINT_LIFE_CYCLE));
        assertDataContent(executionResult,
                containsString(JavaProductModelRepository.RULE_WITH_ENTRY_POINT_DEFAULT_VALUE));
    }

    @Test
    public void shouldReturnOnyRulesWithEntryPoint() {

        ExecutionResult executionResult = graphQL
                .execute("{policyProducts{rules(filter:{withEntryPoints:true}){name}}}");

        assertNoErrors(executionResult);
        assertDataContent(executionResult,
                not(containsString(JavaProductModelRepository.RULE_WITHOUT_ENTRY_POINT_LIFE_CYCLE)));
        assertDataContent(executionResult,
                containsString(JavaProductModelRepository.RULE_WITH_ENTRY_POINT_DEFAULT_VALUE));
    }

    @Test
    public void shouldReturnAllRulesWithoutEntryPoint() {

        ExecutionResult executionResult = graphQL
                .execute("{policyProducts{rules(filter:{withEntryPoints:false}){name}}}");

        assertNoErrors(executionResult);
        assertDataContent(executionResult,
                containsString(JavaProductModelRepository.RULE_WITHOUT_ENTRY_POINT_LIFE_CYCLE));
        assertDataContent(executionResult,
                not(containsString(JavaProductModelRepository.RULE_WITH_ENTRY_POINT_DEFAULT_VALUE)));
    }

    @Test
    public void shouldReturnValidationErrorOnNotExistQuery() {

        ExecutionResult executionResult = graphQL.execute("{wrongQuery}");

        Assert.assertNotNull("Execution result does not contain errors", executionResult.getErrors());
        Assert.assertFalse(executionResult.getErrors().isEmpty());

        GraphQLError graphQLError = executionResult.getErrors().stream().findFirst().orElse(null);

        Assert.assertNotNull(graphQLError);
        Assert.assertThat(graphQLError.getErrorType().name(), equalTo("ValidationError"));
        Assert.assertThat(graphQLError.getMessage(),
                equalTo("Validation error of type FieldUndefined: Field 'wrongQuery' in type 'Query' is undefined @ 'wrongQuery'"));
    }

    @Test
    public void shouldReturnComponentFieldForConnection() {

        ExecutionResult executionResult = graphQL.execute(
                "{policyProducts {" +
                        "  components(filter:{id: \"AutoCSLCoverage\"}) {" +
                        "    connections{connectedTo{" +
                        "      ... on Component {id, description, businessName, type{name}" +
                        "      }}}" +
                        "  }}}");

        assertNoErrors(executionResult);
        assertDataContent(executionResult,
                containsString("components=[" +
                        "{connections=[" +
                            "{connectedTo={" +
                                "id=SingleCoverageGroup, " +
                                "description=null, " +
                                "businessName=Single Coverage Group, " +
                                "type={" +
                                    "name=COVERAGE_GROUP" +
                        "}}}]}"));
    }

    @Test
    public void shouldReturnBehavioursWithValue() {
        ExecutionResult executionResult = graphQL.execute("{policyProducts{behaviours{name parameters{values}}}}");

        assertNoErrors(executionResult);
        assertDataContent(executionResult, containsString("Rateable"));
    }

    private static void assertNoErrors(ExecutionResult result) {
        Assert.assertTrue("Query result contains errors: " + result.getErrors(), result.getErrors().isEmpty());
    }

    private static void assertDataContent(ExecutionResult result, Matcher<String> matcher) {
        Assert.assertNotNull("Execution result data is null", result.getData());
        Assert.assertThat(result.getData().toString(), matcher);
    }
}
