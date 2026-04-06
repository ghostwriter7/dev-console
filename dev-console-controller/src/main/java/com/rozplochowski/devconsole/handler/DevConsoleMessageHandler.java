package com.rozplochowski.devconsole.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rozplochowski.devconsole.DevConsoleMessage;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DevConsoleMessageHandler implements WebSocketHandler {
    private final Map<String, WebSocketSession> sessionByServerName = new HashMap<>();
    private WebSocketSession client;
    private final ObjectMapper mapper;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        log.info("New DevConsole WebSocket connection established with ID: {}", session.getId());
    }

    @Override
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws Exception {
        log.info("Received message from session ID: {}", session.getId());
        var payload = message.getPayload();

        DevConsoleMessage msg = mapper.readValue(payload.toString(), DevConsoleMessage.class);

        if (msg instanceof DevConsoleMessage.ServerConnectCommand(String serviceName)) {
            handleServiceRegistration(serviceName, session);
        } else if (msg instanceof DevConsoleMessage.ClientConnectCommand) {
            log.info("Received ClientConnectCommand from {}. UI connected.", session.getId());
            client = session;
        } else if (msg instanceof DevConsoleMessage.ExecuteScriptCommand executeScriptCommand) {
            var targetSession = sessionByServerName.get(executeScriptCommand.targetServiceName());
            if (targetSession != null) {
                targetSession.sendMessage(message);
            } else {
                log.error("No session found for target service: {}", executeScriptCommand.targetServiceName());
            }
        } else if (msg instanceof DevConsoleMessage.ScriptExecutedEvent scriptExecutedEvent) {
            log.info("Received ScriptExecutedEvent from Session: {} for UUID: {}. Replaying back to UI.", session.getId(), scriptExecutedEvent.uuid());
            client.sendMessage(message);
        }

    }

    private void handleServiceRegistration(String serviceName, @NonNull WebSocketSession session) throws IOException {
        log.info("Received ServerConnectCommand from {}", serviceName);
        sessionByServerName.put(serviceName, session);

        var payload = new DevConsoleMessage.ServerConnectedEvent(serviceName);
        var message = mapper.writeValueAsString(payload);
        var response = new TextMessage(message);
        session.sendMessage(response);
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        log.error("DevConsole WebSocket transport error with ID: {}", session.getId());
        log.error("Exception: ", exception);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) throws Exception {
        log.info("DevConsole WebSocket connection closed with ID: {}", session.getId());
//        sessions.remove(session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
