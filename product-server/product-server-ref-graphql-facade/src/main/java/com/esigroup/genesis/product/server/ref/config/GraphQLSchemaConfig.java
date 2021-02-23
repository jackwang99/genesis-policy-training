/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.esigroup.genesis.product.server.ref.config;

import java.util.Map;

import com.coxautodev.graphql.tools.SchemaParser;
import com.eisgroup.genesis.design.specification.BuildingBlock;
import com.eisgroup.genesis.design.specification.repo.BuildingBlockRepository;
import com.eisgroup.genesis.design.tooling.facade.dto.ProductDTO;
import com.eisgroup.genesis.design.tooling.model.Classification;
import com.eisgroup.genesis.design.tooling.repo.ClassificationRepository;
import com.eisgroup.genesis.design.tooling.repo.ProductRepository;
import com.eisgroup.genesis.product.specification.general.connection.Reference;
import com.eisgroup.genesis.product.specification.general.impl.AttributeImpl;
import com.eisgroup.genesis.product.specification.general.impl.ConstantBehaviourImpl;
import com.eisgroup.genesis.product.specification.general.impl.DefaultComponent;
import com.eisgroup.genesis.product.specification.general.impl.RuleBehaviourImpl;
import com.eisgroup.genesis.product.specification.general.impl.RuleImpl;
import com.eisgroup.genesis.product.specification.policy.PolicyModel;
import com.eisgroup.genesis.product.specification.policy.impl.BusinessDimensionImpl;
import com.eisgroup.genesis.product.specification.policy.impl.EntryPointImpl;
import com.eisgroup.genesis.product.specification.repo.ProductModelRepository;
import com.esigroup.genesis.product.server.config.GraphQLServletConfig;
import com.esigroup.genesis.product.server.graphql.model.BehaviourValue;
import com.esigroup.genesis.product.server.graphql.resolver.ConstantBehaviourValueResolver;
import com.esigroup.genesis.product.server.graphql.resolver.PolicySpecificationResolver;
import com.esigroup.genesis.product.server.graphql.resolver.RuleBehaviourValueResolver;
import com.esigroup.genesis.product.server.graphql.services.BehaviourValueConverter;
import com.esigroup.genesis.product.services.filter.ProductModelFilter;
import graphql.schema.GraphQLSchema;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring boot autoconfiguration with the GraphQL schema bean,
 * as soon as GraphQLSchema bean is defined the application will have the
 * GraphQL servlet being registered
 *
 * @author dlevchuk
 */
@Configuration
@AutoConfigureBefore(GraphQLServletConfig.class)
public class GraphQLSchemaConfig {

    private static final String CONSTANT_BEHAVIOUR_TYPE_ALIAS = "ConstantBehaviour";

    private static final String RULE_BEHAVIOUR_TYPE_ALIAS = "RuleBehaviour";

    private static final String POLICY_PRODUCT_TYPE_ALIAS = "PolicyProduct";

    private static final String PRODUCT_TYPE_ALIAS = "Product";

    private static final String CLASSIFICATION_TYPE_ALIAS = "Classification";

    private static final String BUILDING_BLOCK_TYPE_ALIAS =  "BuildingBlock";

    @Bean
    public GraphQLSchema schemaExtension(ProductModelRepository repository,
                                         ProductModelFilter productModelFilter,
                                         BehaviourValueConverter behaviourValueConverter,
                                         @Qualifier("designProductRepository") ProductRepository designProductRepository,
                                         @Qualifier("classificationRepository") ClassificationRepository classificationRepository,
                                         BuildingBlockRepository buildingBlockRepository) {
        return SchemaParser.newParser()
                // Merge core and extension schemas into one
                //
                .files("schema-component.graphqls",
                        "schema-extension.graphqls",
                        "schema-tooling.graphqls"
                )
                .resolvers(new com.esigroup.genesis.product.server.ref.graphql.extension.Query(
                                repository,
                                productModelFilter,
                                behaviourValueConverter,
                                designProductRepository,
                                classificationRepository,
                                buildingBlockRepository),
                        new PolicySpecificationResolver(),
                        new ConstantBehaviourValueResolver(behaviourValueConverter),
                        new RuleBehaviourValueResolver(behaviourValueConverter)
                        // Tooling resolvers
                        //

                )
                .dictionary(
                        DefaultComponent.class,
                        AttributeImpl.class,
                        EntryPointImpl.class,
                        RuleImpl.class,
                        BusinessDimensionImpl.class,
                        Reference.class,
                        BehaviourValue.class
                        // Tooling Dictionary
                        //

                        )
                .dictionary(Map.of(
                        CONSTANT_BEHAVIOUR_TYPE_ALIAS, ConstantBehaviourImpl.class,
                        RULE_BEHAVIOUR_TYPE_ALIAS, RuleBehaviourImpl.class,
                        POLICY_PRODUCT_TYPE_ALIAS, PolicyModel.class,
                        // Tooling Dictionary
                        //
                        PRODUCT_TYPE_ALIAS, ProductDTO.class,
                        CLASSIFICATION_TYPE_ALIAS, Classification.class,
                        BUILDING_BLOCK_TYPE_ALIAS, BuildingBlock.class
                        ))
                .build()
                .makeExecutableSchema();
    }
}
