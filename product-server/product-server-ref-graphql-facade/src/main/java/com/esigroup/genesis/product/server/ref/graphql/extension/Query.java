/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.esigroup.genesis.product.server.ref.graphql.extension;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.eisgroup.genesis.design.specification.BuildingBlock;
import com.eisgroup.genesis.design.specification.ClassificationAware;
import com.eisgroup.genesis.design.specification.repo.BuildingBlockRepository;
import com.eisgroup.genesis.design.specification.repo.query.ClassificationAwarePredicateQuery;
import com.eisgroup.genesis.design.tooling.facade.dto.ClassificationFilter;
import com.eisgroup.genesis.design.tooling.facade.dto.ProductDTO;
import com.eisgroup.genesis.design.tooling.facade.dto.ProductFilter;
import com.eisgroup.genesis.design.tooling.model.Classification;
import com.eisgroup.genesis.design.tooling.model.Product;
import com.eisgroup.genesis.design.tooling.repo.ClassificationRepository;
import com.eisgroup.genesis.design.tooling.repo.ProductRepository;
import com.eisgroup.genesis.product.specification.policy.PolicyModel;
import com.eisgroup.genesis.product.specification.repo.ProductModelRepository;
import com.esigroup.genesis.product.server.graphql.model.Filter;
import com.esigroup.genesis.product.server.graphql.resolver.QueryResolver;
import com.esigroup.genesis.product.server.graphql.services.BehaviourValueConverter;
import com.esigroup.genesis.product.services.filter.ProductModelFilter;
import org.apache.commons.lang3.StringUtils;

/**
 * Extend the core {@link QueryResolver} with additional query
 *
 * @author dlevchuk
 */
public class Query extends QueryResolver {

    private static final String ALL_CLASSIFICATION_VALUES = "All";

    private ProductRepository designProductsRepository;

    private ClassificationRepository classificationRepository;

    private BuildingBlockRepository buildingBlockRepository;

    public Query(ProductModelRepository productModelRepository,
                 ProductModelFilter productModelFilter,
                 BehaviourValueConverter behaviourValueConverter,
                 ProductRepository designProductsRepository,
                 ClassificationRepository classificationRepository,
                 BuildingBlockRepository buildingBlockRepository) {
        super(productModelRepository, productModelFilter, behaviourValueConverter);
        this.classificationRepository = classificationRepository;
        this.designProductsRepository = designProductsRepository;
        this.buildingBlockRepository = buildingBlockRepository;
    }

    public PolicyModel productByCd(String productCd) {
        // FIXME: productByProductCd should return single item

        Filter filter = new Filter();
        filter.setProductCd(productCd);

        return policyProducts(filter).stream().findFirst().orElse(null);
    }

    @Deprecated
    public Collection<String> listClassificationNames() {
        return classificationRepository.listClassificationNames();
    }

    @Deprecated
    public Collection<String> listClassificationValues(ClassificationFilter filter) {
        if (filter == null) {
            return classificationRepository.listClassificationValues();
        }
        return classificationRepository.valuesByName(filter.getClassificationName());
    }

    @Deprecated
    public Collection<ProductDTO> listProducts(ProductFilter filter) {
        Optional<Predicate<Product>> productPredicate = createPredicate(filter);

        if (productPredicate.isPresent()) {
            return designProductsRepository.listProducts().stream()
                    .filter(productPredicate.get())
                    .map(p -> new ProductDTO(p))
                    .collect(Collectors.toList());
        }

        return designProductsRepository.listProducts().stream()
                .map(p -> new ProductDTO(p))
                .collect(Collectors.toList());
    }

    public Collection<Classification> listClassifications(ClassificationFilter filter) {
        if (filter == null) {
            return classificationRepository.listClassifications();
        }

        return classificationRepository.listClassifications().stream()
                .filter(c -> c.getName().equals(filter.getClassificationName()))
                .collect(Collectors.toList());
    }

    private Optional<Predicate<Product>> createPredicate(ProductFilter filter) {
        if (filter == null) {
            return Optional.empty();
        }

        Predicate<Classification> namePredicate = c -> filter.getClassificationName().equals(c.getName());
        Predicate<Classification> valuePredicate =
                c -> c.getValues().stream().anyMatch(s -> filter.getClassificationValue().equals(s));

        if (StringUtils.isNotEmpty(filter.getClassificationName())
                && StringUtils.isNotEmpty(filter.getClassificationValue())) {
            return Optional.of(product -> product.getClassifications().stream()
                    .anyMatch(c -> namePredicate.test(c) && valuePredicate.test(c)));
        } else if (StringUtils.isNotEmpty(filter.getClassificationName())) {
           return Optional.of(product -> product.getClassifications().stream().anyMatch(namePredicate));
        } else if (StringUtils.isNotEmpty(filter.getClassificationValue())) {
            return Optional.of(product -> product.getClassifications().stream().anyMatch(valuePredicate));
        }

        return Optional.empty();
    }

    public Collection<BuildingBlock> listBuildingBlocks(ClassificationFilter filter) {
        return buildingBlockRepository.findByClassifications(
                new ClassificationAwarePredicateQuery(createClassificationPredicate(filter)));
    }

    private Predicate<ClassificationAware> createClassificationPredicate(ClassificationFilter filter) {
        return filter == null ? c -> true : classificationAware -> classificationAware.getClassifications().stream()
                .anyMatch(getClassificationPredicate(filter));
    }

    private Predicate<Classification> getClassificationPredicate(ClassificationFilter filter) {
        return classification -> Objects.equals(classification.getName(), filter.getClassificationName())
                && (StringUtils.isEmpty(filter.getClassificationValue())
                    || ALL_CLASSIFICATION_VALUES.equalsIgnoreCase(filter.getClassificationValue())
                    || classification.getValues().contains(filter.getClassificationValue()));
    }
}
