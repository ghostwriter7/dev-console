package com.rozplochowski.devconsole.websocket.handlers.commands;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.Handles;
import com.rozplochowski.devconsole.MessageHandler;
import com.rozplochowski.devconsole.common.MessageSender;
import com.rozplochowski.devconsole.websocket.TypeMetadataFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@Slf4j
@Component
@Handles(CommandOrEvent.ComputeTypeMetadataCommand.class)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ComputeTypeMetadataCommandHandler implements MessageHandler<CommandOrEvent.ComputeTypeMetadataCommand> {
    private final MessageSender sender;

    @Override
    public void handle(CommandOrEvent.ComputeTypeMetadataCommand message, WebSocketSession session) {
        log.info("Received ComputeTypeMetadataCommand for type: {}", message.fullName());
        var command = tryGetTypeMetadata(message.fullName(), message.id());
        sender.send(session, command);
    }

    private CommandOrEvent.TypeMetadataComputedEvent tryGetTypeMetadata(String fullName, UUID id) {
        try {
            var clazz = Class.forName(fullName);
            var typeMetadata = TypeMetadataFactory.from(clazz);
            return new CommandOrEvent.TypeMetadataComputedEvent(typeMetadata, id);
        } catch (ClassNotFoundException e) {
            log.error("Could not find class: {}", fullName, e);
            return new CommandOrEvent.TypeMetadataComputedEvent(null, id);
        }
    }
}
