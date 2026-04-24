package com.rozplochowski.devconsole.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.common.MessageMediator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import static com.rozplochowski.devconsole.SessionAttribute.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class TargetServiceWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper mapper;
    private final MessageMediator mediator;
    private final SessionInitializer initializer;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        initializer.initialize(session);
    }

    @Override
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws Exception {
        var payload = message.getPayload();
        log.info("Received message: {}", payload);

        var actualMessage = mapper.readValue(payload.toString(), CommandOrEvent.class);

        mediator.dispatch(session, actualMessage);
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        log.info("DevConsole WebSocket transport error.");
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) {
        log.info("DevConsole WebSocket connection closed.");
        var attributes = session.getAttributes();
        attributes.remove(BINDING.name());
        attributes.remove(SHELL.name());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
