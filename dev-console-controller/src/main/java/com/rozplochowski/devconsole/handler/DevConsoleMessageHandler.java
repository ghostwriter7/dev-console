package com.rozplochowski.devconsole.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.SessionAttribute;
import com.rozplochowski.devconsole.SessionIdReader;
import com.rozplochowski.devconsole.UiClientToken;
import com.rozplochowski.devconsole.common.MessageMediator;
import com.rozplochowski.devconsole.handler.repository.SessionRepository;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DevConsoleMessageHandler implements WebSocketHandler {

    private final SessionRepository sessionRepository;
    private final ObjectMapper mapper;
    private final MessageMediator messageMediator;
    private final AvailableServicesChangedNotifier availableServicesChangedNotifier;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        log.info("New DevConsole WebSocket connection established with ID: {}", session.getId());
    }

    @Override
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws JsonProcessingException {
        var payload = message.getPayload();

        CommandOrEvent msg = mapper.readValue(payload.toString(), CommandOrEvent.class);

        log.info("Received message from: '{}' of type: {}", SessionIdReader.read(session), msg.getClass().getSimpleName());

        messageMediator.dispatch(session, msg);
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {
        log.error("DevConsole WebSocket transport error with ID: {}", session.getId());
        log.error("Exception: ", exception);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) {
        log.info("DevConsole WebSocket connection closed with ID: {}", SessionIdReader.read(session));
        var serviceName = (String) session.getAttributes().get(SessionAttribute.SERVICE_NAME.name());

        if (serviceName != null) {
            sessionRepository.remove(serviceName);

            if (!serviceName.equals(UiClientToken.VALUE)) {
                availableServicesChangedNotifier.notifyClient();
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
