package com.rozplochowski.devconsole.handler.handlers;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.handler.MessageSender;
import com.rozplochowski.devconsole.handler.SessionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
@Handles(CommandOrEvent.ScriptExecutedEvent.class)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ScriptExecutedEventHandler implements MessageHandler<CommandOrEvent.ScriptExecutedEvent> {
    private final MessageSender sender;
    private final SessionRepository sessionRepository;

    @Override
    public void handle(CommandOrEvent.ScriptExecutedEvent message, WebSocketSession session) {
        log.info("Received ScriptExecutedEvent from Session: {} for UUID: {}. Replaying back to UI.", session.getId(), message.uuid());
        sessionRepository.getClient().ifPresent(client -> sender.send(client, message));
    }
}
