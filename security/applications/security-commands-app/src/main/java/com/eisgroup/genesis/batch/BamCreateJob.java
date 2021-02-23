/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.batch;

import com.eisgroup.genesis.bam.ActivityStatus;
import com.eisgroup.genesis.bam.events.ActivityEventPublisher;
import com.eisgroup.genesis.bam.events.ActivityStreamEvent;
import com.eisgroup.genesis.bam.events.ActivityStreamEventBuilderFactory;
import com.eisgroup.genesis.factory.modeling.types.immutable.User;
import com.eisgroup.genesis.jobs.lifecycle.api.handler.CompletableCommandHandler;
import com.eisgroup.genesis.json.key.RootEntityKey;
import com.eisgroup.genesis.repository.TargetEntityNotFoundException;
import com.eisgroup.genesis.security.CurrentUserAccess;
import com.eisgroup.genesis.security.repository.SecurityRepository;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

import static java.util.Collections.singleton;

/**
 * A POC implementation of a job that creates a BAM event for the user.
 * The user id is expected to be provided as a job parameter. For more information,
 * see {@link BamCreateRequest}.
 *
 * @author gvisokinskas
 */
public class BamCreateJob extends CompletableCommandHandler<BamCreateRequest> {
    public static final String NAME = "createUserBam";
    private static final String ACTIVITY_ID = "JobActivity";

    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    private ActivityEventPublisher activityEventPublisher;

    @Autowired
    private ActivityStreamEventBuilderFactory eventBuilderFactory;

    @Value("${genesis.user.model}")
    private String userModelName;

    @Override
    public Completable doExecute(BamCreateRequest bamCreateRequest) {
        return parseRequestKey(bamCreateRequest)
                .flatMap(key -> securityRepository.loadUser(singleton(key), userModelName)
                        .firstElement()
                        .switchIfEmpty(Maybe.error(TargetEntityNotFoundException::new))
                        .map(User::getUuid))
                .switchIfEmpty(Maybe.defer(() -> Maybe.just(CurrentUserAccess.resolveCurrentUser()
                                                                    .orElseThrow(IllegalStateException::new)
                                                                    .getUserId().toString())))
                .flatMapCompletable(this::publish);
    }

    private Completable publish(String uid) {
        return Completable.fromAction(() -> activityEventPublisher.publish(activityEvent(uid)));
    }

    private Maybe<RootEntityKey> parseRequestKey(BamCreateRequest bamCreateRequest) {
        return Optional.ofNullable(bamCreateRequest.getKey())
                .map(Maybe::just)
                .orElseGet(Maybe::empty);
    }

    private ActivityStreamEvent activityEvent(String uuid) {
        return eventBuilderFactory.createBuilder()
                .entityId(ACTIVITY_ID)
                .entityNumber(uuid)
                .messageId(getVariation().getName() + "_" + getName())
                .status(ActivityStatus.FINISHED)
                .build();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
