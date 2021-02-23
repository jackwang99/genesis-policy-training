package com.eisgroup.genesis.modules;

import com.eisgroup.genesis.commands.communication.WriteHandlerRI;
import com.eisgroup.genesis.commands.communication.conversion.ConversionWriteHandler;
import com.eisgroup.genesis.commands.crm.communication.WriteHandler;
import com.eisgroup.genesis.crm.commands.ConversionCommands;
import com.eisgroup.genesis.lifecycle.CommandHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ComponentScan("com.eisgroup.genesis.commands.listener")
public class GenesisCommunicationLifecycleModule extends CommunicationLifecycleModule {

    @Override
    public Map<String, CommandHandler<?, ?>> getCommandHandlers() {
        Map<String, CommandHandler<?, ?>> handlers = super.getCommandHandlers();
        handlers.put(WriteHandler.NAME, new WriteHandlerRI());
        handlers.put(ConversionCommands.WRITE, new ConversionWriteHandler());
        return handlers;
    }

    @Override
    public String getModelName() {
        return "Communication";
    }
}
