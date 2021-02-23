/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.eisgroup.genesis.commands.request.IdentifierRequest;
import com.eisgroup.genesis.comparison.ComparisonService;
import com.eisgroup.genesis.comparison.diff.Diff;
import com.eisgroup.genesis.comparison.filter.CompareFilter;
import com.eisgroup.genesis.comparison.impl.path.JsonElementPath;
import com.eisgroup.genesis.comparison.representation.DiffPatchEntity;
import com.eisgroup.genesis.lifecycle.executor.CommandExecutionContext;
import com.eisgroup.genesis.factory.json.VariableEntity;
import com.eisgroup.genesis.factory.modeling.types.PolicySummary;
import com.eisgroup.genesis.json.JsonEntity;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.policy.core.lifecycle.commands.quote.QuoteCommandHandler;
import com.eisgroup.genesis.policy.core.versioning.Operation;
import com.eisgroup.genesis.policy.core.versioning.OperationKey;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.PersonalAutoCommands;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.OfferImpactDiffHolder;
import com.eisgroup.genesis.policy.personal.auto.lifecycle.commands.quote.harmonization.OfferImpactResolver;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterables;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Handler for harmonization of quote offer.
 *
 * @author yratkevich
 * @since 9.16
 */
public class HarmonizeHandler extends QuoteCommandHandler<IdentifierRequest, PolicySummary> {

    private final Comparator<Operation> operationTimestampComparator = Comparator.comparing(operation -> operation.getKey().getTimestamp());
    private static final Logger LOGGER = LoggerFactory.getLogger(HarmonizeHandler.class);

    @Autowired
    @Qualifier("defaultComparisonService")
    private ComparisonService comparisonService;

    @Autowired
    private Collection<com.eisgroup.genesis.comparison.operation.Operation> operationActions;

    @Autowired
    @Qualifier("defaultOfferImpactResolver")
    private OfferImpactResolver offerImpactResolver;

    @Override
    public String getName() {
        return PersonalAutoCommands.HARMONIZE;
    }

    @Nonnull
    @Override
    public Single<PolicySummary> execute(@Nonnull IdentifierRequest identifierRequest, @Nonnull PolicySummary entity) {
        List<Operation> aggregatedOperations = new LinkedList<>();
        RootEntityKey rootKey = identifierRequest.getKey();

        JsonObject targetObject = CommandExecutionContext.getCurrentInstance().getEntitySnapshot().toJson();
        return findDiffs(rootKey, aggregatedOperations)
                .andThen(Single.fromCallable(() -> {
                    JsonObject sourceObject = targetObject.deepCopy();
                    aggregatedOperations.stream()
                            .sorted(operationTimestampComparator.reversed())
                            .forEach(operationRecord ->
                                    operationActions.forEach(operationAction ->
                                            operationAction.rollback(new DiffPatchEntity(new JsonObject()) {
                                                @Override
                                                public List<Diff> getDiffs() {
                                                    return new ArrayList<>(operationRecord.getDiffs());
                                                }
                                            }, sourceObject)));
                    return sourceObject;
                }))
                .flatMap(sourceObject -> comparisonService.compare(sourceObject, targetObject, new CompareFilter(retrieveDomainModel())))
                .flatMap(diffPatch -> {
                            LOGGER.warn("Using offerImpactResolver: {}", offerImpactResolver.getClass().getName());
                            return offerImpactResolver.applyDiffs(diffPatch.getDiffs().stream()
                                            .map(diff -> {
                                                JsonElementPath diffPath = new JsonElementPath(diff.getPath(), retrieveDomainModel());
                                                return new OfferImpactDiffHolder(
                                                        resolveModelName(diffPath, diff, targetObject),
                                                        Iterables.getLast(diffPath.getPathTokens()).getRaw(),
                                                        diff);
                                            })
                                            .collect(Collectors.toList()),
                                    entity);
                        }
                );
    }

    private String resolveModelName(JsonElementPath diffPath, Diff diff, JsonObject targetObject) {
        JsonElement modelHolder = resolveModelHolder(diffPath, diff, targetObject);
        JsonElement modelType = resolveModelType(modelHolder);
        return modelType == null
                ? OfferImpactDiffHolder.NOT_SPECIFIED_MODEL
                : modelType.getAsString();
    }

    private JsonElement resolveModelHolder(JsonElementPath diffPath, Diff diff, JsonObject targetObject) {
        JsonElement diffElement = MoreObjects.firstNonNull(diff.getSourceValue(), diff.getTargetValue());
        return diffElement.isJsonPrimitive()
                ? diffPath.parent().getElement(targetObject).getRight()
                : diffElement;
    }

    @Nullable
    private JsonElement resolveModelType(JsonElement modelHolder) {
        return modelHolder.isJsonObject()
                ? modelHolder.getAsJsonObject().get(JsonEntity.TYPE_ATTRIBUTE)
                : null;
    }

    private Completable findDiffs(RootEntityKey rootKey, List<Operation> aggregatedOperations) {
        return quoteReadRepo.load(rootKey, retrieveDomainModel())
                .flatMap(quote -> policyReadRepo.loadPolicyOperations(rootKey, retrieveDomainModel(), ((VariableEntity) quote).getVariation())
                        .toList()
                        .flatMapMaybe(operations -> {
                            Optional<Operation> completeOfferOperation = operations.stream()
                                    .filter(operation -> operation.getOperation().equals(PersonalAutoCommands.COMPLETE_OFFER))
                                    .max(operationTimestampComparator);

                            return completeOfferOperation
                                    .map(operation -> operation.getKey().getTimestamp())
                                    .map(completeOfferTimestamp -> {
                                        aggregateOperations(aggregatedOperations, operations, completeOfferTimestamp);
                                        return Maybe.<Long>empty();
                                    })
                                    .orElseGet(() -> {
                                        aggregateOperations(aggregatedOperations, operations, null);
                                        return Optional.ofNullable(quote.getCreatedFromQuoteRev())
                                                .map(Maybe::just)
                                                .orElseGet(Maybe::empty);
                                    });
                        }))
                .flatMapCompletable(parentQuoteRevisionNo -> findDiffs(new RootEntityKey(rootKey.getRootId(), parentQuoteRevisionNo.intValue()), aggregatedOperations));
    }

    private void aggregateOperations(List<Operation> aggregatedOperations, List<Operation> operations, LocalDateTime completeOfferTimestamp) {
        LocalDateTime firstOperationTimestamp = aggregatedOperations.stream()
                .min(operationTimestampComparator)
                .map(Operation::getKey)
                .map(OperationKey::getTimestamp)
                .orElse(null);

        aggregatedOperations.addAll(0, operations.stream()
                .filter(operation -> {
                    LocalDateTime timestamp = operation.getKey().getTimestamp();
                    return (completeOfferTimestamp == null || timestamp.isAfter(completeOfferTimestamp))
                            && (firstOperationTimestamp == null || timestamp.isBefore(firstOperationTimestamp));
                })
                .collect(Collectors.toList()));
    }
}