/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.crm.events.listener;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eisgroup.genesis.command.Command;
import com.eisgroup.genesis.command.result.CommandResult;
import com.eisgroup.genesis.commands.publisher.api.CommandPublisher;
import com.eisgroup.genesis.commands.streams.ResponseEnvelope;
import com.eisgroup.genesis.communication.ClientContext;
import com.eisgroup.genesis.model.Variation;
import com.eisgroup.genesis.streams.HostnameMessageMetadata;
import com.eisgroup.genesis.streams.MessageMetadata;
import com.eisgroup.genesis.streams.consumer.StreamMessageHandler;
import com.google.gson.JsonElement;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * 
 * @author Dmitry Andronchik
 * @since 9.13
 */
public class CustomerCommandEventHandler implements StreamMessageHandler<ResponseEnvelope<CommandResult>> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerCommandEventHandler.class);

    private static final String HOST_NAME = System.getProperty("HOSTNAME",
            System.getenv().getOrDefault("NM_HOST", "CustomerCommandEventHandler"));

    private final CommandPublisher publisher;

    private Subject<ResponseEnvelope<CommandResult>> responsePipe = PublishSubject.create();

    public CustomerCommandEventHandler(CommandPublisher publisher) {
        this.publisher = publisher;
    }

    public Single<CommandResult> executeCommand(String commandName, String modelName, JsonElement jsonElement) {

        Command command = new Command(Variation.INVARIANT.getName(), commandName, jsonElement);

        // not important, used for UI tracking/logging
        String requestId = UUID.randomUUID().toString();

        // used to map response back to request
        String channelId = UUID.randomUUID().toString();

        return Completable.fromRunnable(() -> {
            ClientContext clientCtx = new ClientContext(channelId, requestId, HOST_NAME);
            // send out command
            publisher.publish(command, modelName, clientCtx);
        })
                // flat map to response
                .andThen(Single.defer(() -> {
                    Subject<CommandResult> resultPipe = AsyncSubject.<CommandResult>create().toSerialized();
                    Disposable disposable = responsePipe.filter(response -> response.getCallerId().equals(channelId))
                            .map(ResponseEnvelope::getMessage).subscribe(result -> {
                                resultPipe.onNext(result);
                                // close pipe for blockingGet to work, if not
                                // blocking (using proper async subscribe)
                                // can use responsePipe directly
                                resultPipe.onComplete();
                            });
                    
                    return resultPipe
                            .singleOrError()
                            .doFinally(disposable::dispose);
                }));
    }

    @Override
    public Completable handle(ResponseEnvelope<CommandResult> response) {
        return Completable.fromRunnable(() -> responsePipe.onNext(response));
    }

    @Override
    public boolean supports(MessageMetadata message) {
        if (message instanceof HostnameMessageMetadata) {
            String actualHostname = ((HostnameMessageMetadata) message).getHostname();
            LOG.debug("Message metadata match, expected {}, got {}", HOST_NAME, actualHostname);
            return HOST_NAME.equals(actualHostname);
        }
        return false;
    }
}
