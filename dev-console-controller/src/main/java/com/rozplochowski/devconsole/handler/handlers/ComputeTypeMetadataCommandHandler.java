package com.rozplochowski.devconsole.handler.handlers;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.Handles;
import com.rozplochowski.devconsole.common.MessageSender;
import com.rozplochowski.devconsole.handler.SessionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
@Handles(CommandOrEvent.ComputeTypeMetadataCommand.class)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ComputeTypeMetadataCommandHandler implements com.rozplochowski.devconsole.MessageHandler<CommandOrEvent.ComputeTypeMetadataCommand> {
    private final MessageSender sender;
    private final SessionRepository sessionRepository;

    @Override
    public void handle(CommandOrEvent.ComputeTypeMetadataCommand command, WebSocketSession session) {
        sessionRepository.tryGet(command.targetServiceName())
                .ifPresentOrElse(targetSession -> sender.send(targetSession, command),
                        () -> log.error("No session found for target service: {}", command.targetServiceName()));
    }
}
