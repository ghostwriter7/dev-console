package com.rozplochowski.devconsole.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.Symbol;
import com.rozplochowski.devconsole.TypeMetadata;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DevConsoleMessageHandler implements WebSocketHandler {
    private final Map<String, WebSocketSession> sessionByServerName = new HashMap<>();
    private final Map<String, ServerContext> serverContextMap = new HashMap<>();
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

        CommandOrEvent msg = mapper.readValue(payload.toString(), CommandOrEvent.class);

        if (msg instanceof CommandOrEvent.ServerConnectCommand(String serviceName, List<TypeMetadata> typeMetadata, List<Symbol> symbols)) {
            handleServiceRegistration(serviceName, session);
            handleTypeMetadataAndSymbols(serviceName, typeMetadata, symbols);
        } else if (msg instanceof CommandOrEvent.ClientConnectCommand) {
            log.info("Received ClientConnectCommand from {}. UI connected.", session.getId());
            client = session;
            notifyClientAboutAvailableServices();
            if (!serverContextMap.isEmpty()) {
                serverContextMap.forEach((serviceName, context) -> {
                    send(client, new CommandOrEvent.InstallTypeMetadataCommand(serviceName, context.typeMetadata(), context.symbols()));
                });
            }
        } else if (msg instanceof CommandOrEvent.ExecuteScriptCommand executeScriptCommand) {
            var targetSession = sessionByServerName.get(executeScriptCommand.targetServiceName());
            if (targetSession != null) {
                targetSession.sendMessage(message);
            } else {
                log.error("No session found for target service: {}", executeScriptCommand.targetServiceName());
            }
        } else if (msg instanceof CommandOrEvent.ScriptExecutedEvent scriptExecutedEvent) {
            log.info("Received ScriptExecutedEvent from Session: {} for UUID: {}. Replaying back to UI.", session.getId(), scriptExecutedEvent.uuid());
            client.sendMessage(message);
        }

    }

    private void handleTypeMetadataAndSymbols(String serviceName, List<TypeMetadata> typeMetadata, List<Symbol> symbols) {
        serverContextMap.put(serviceName, new ServerContext(typeMetadata, symbols));
        if (client != null) {
            var payload = new CommandOrEvent.InstallTypeMetadataCommand(serviceName, typeMetadata, symbols);
            send(client, payload);
        }
    }

    private void notifyClientAboutAvailableServices() {
        var availableServices = sessionByServerName.keySet().stream().toList();
        var payload = new CommandOrEvent.AvailableServicesChangedEvent(availableServices);
        send(client, payload);
    }

    private void handleServiceRegistration(String serviceName, @NonNull WebSocketSession session) throws IOException {
        log.info("Received ServerConnectCommand from {}", serviceName);
        sessionByServerName.put(serviceName, session);
        session.getAttributes().put("serviceName", serviceName);

        var payload = new CommandOrEvent.ServerConnectedEvent(serviceName);
        var message = mapper.writeValueAsString(payload);
        var response = new TextMessage(message);
        session.sendMessage(response);

        if (client != null) {
            notifyClientAboutAvailableServices();
        }
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        log.error("DevConsole WebSocket transport error with ID: {}", session.getId());
        log.error("Exception: ", exception);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) throws Exception {
        log.info("DevConsole WebSocket connection closed with ID: {}", session.getId());
        var serviceName = (String) session.getAttributes().get("serviceName");

        if (serviceName != null) {
            sessionByServerName.remove(serviceName);
            notifyClientAboutAvailableServices();
        }

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void send(WebSocketSession session, CommandOrEvent message) {
        try {
            var textMessage = new TextMessage(serialize(message));
            session.sendMessage(textMessage);
        } catch (IOException ex) {
            log.error("Failed to send message to WebSocket session: {}", session.getId(), ex);
        }
    }

    private String serialize(CommandOrEvent message) {
        try {
            return mapper.writeValueAsString(message);
        } catch (IOException e) {
            throw new RuntimeException("Could not serialize JSON: " + e.getMessage());
        }
    }
}
