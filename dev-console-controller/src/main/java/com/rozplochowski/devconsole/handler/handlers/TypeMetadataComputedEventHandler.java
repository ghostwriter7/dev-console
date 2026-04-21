package com.rozplochowski.devconsole.handler.handlers;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.Handles;
import com.rozplochowski.devconsole.MessageHandler;
import com.rozplochowski.devconsole.common.MessageSender;
import com.rozplochowski.devconsole.handler.repository.SessionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
@Handles(CommandOrEvent.TypeMetadataComputedEvent.class)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TypeMetadataComputedEventHandler implements MessageHandler<CommandOrEvent.TypeMetadataComputedEvent> {
    private final MessageSender sender;
    private final SessionRepository sessionRepository;

    @Override
    public void handle(CommandOrEvent.TypeMetadataComputedEvent message, WebSocketSession session) {
        sessionRepository.tryGetClient()
                .ifPresentOrElse(client -> sender.send(client, message),
                        () -> log.error("No client session found. Failed to send TypeMetadataComputedEvent."));
    }
}
