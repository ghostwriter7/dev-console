package com.rozplochowski.devconsole.handler.handlers;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.Handles;
import com.rozplochowski.devconsole.MessageHandler;
import com.rozplochowski.devconsole.common.MessageSender;
import com.rozplochowski.devconsole.handler.SessionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
@Handles(CommandOrEvent.ExecuteScriptCommand.class)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ExecuteScriptCommandHandler implements MessageHandler<CommandOrEvent.ExecuteScriptCommand> {
    private final SessionRepository sessionRepository;
    private final MessageSender sender;

    @Override
    public void handle(CommandOrEvent.ExecuteScriptCommand command, WebSocketSession session) {
        // TODO Consider what should happen if a target service is unavailable. Should I notify UI immediately?
        sessionRepository.tryGet(command.targetServiceName())
                .ifPresentOrElse(targetSession -> sender.send(targetSession, command),
                        () -> log.error("No session found for target service: {}", command.targetServiceName()));
    }
}
