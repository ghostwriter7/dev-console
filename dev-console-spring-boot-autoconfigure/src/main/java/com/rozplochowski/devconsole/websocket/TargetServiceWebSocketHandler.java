package com.rozplochowski.devconsole.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.Symbol;
import com.rozplochowski.devconsole.TypeMetadata;
import com.rozplochowski.devconsole.common.MessageMediator;
import groovy.lang.Binding;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.*;

import static com.rozplochowski.devconsole.websocket.SessionAttribute.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class TargetServiceWebSocketHandler implements WebSocketHandler {

    private final ApplicationContext context;
    private final Environment environment;
    private final ObjectMapper mapper;
    private final MessageMediator mediator;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        log.info("New DevConsole WebSocket connection established.");

        Map<String, Object> vars = new HashMap<>();
        vars.put(CTX.name(), context);
        vars.put(ENV.name(), environment);
        vars.put(SESSION.name(), session);

        Binding binding = new Binding(vars);
        session.getAttributes().put(BINDING.name(), binding);

        List<TypeMetadata> typeMetadataList = new ArrayList<>();
        List<Symbol> symbols = new ArrayList<>();
        for (var name : context.getBeanDefinitionNames()) {
            if (!name.contains("springframework") && !name.contains("jpa") && !name.endsWith("Controller")) {
                var bean = context.getBean(name);
                typeMetadataList.add(TypeMetadataFactory.from(bean.getClass()));
                symbols.add(new Symbol(name, TypeMetadataFactory.toTypeInfo(bean.getClass())));
                vars.put(name, bean);
            }
        }

        var shell = new GroovyShellFactory().fromBinding(binding);
        session.getAttributes().put(SHELL.name(), shell);

        var appName = getAppName();
        log.info("Sending initial WebSocket message for app: {}", appName);
        var payload = new CommandOrEvent.ServerConnectCommand(appName, typeMetadataList, symbols);
        var message = mapper.writeValueAsString(payload);

        session.sendMessage(new TextMessage(message));
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

    private String serialize(CommandOrEvent message) {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Could not serialize JSON: " + ex.getMessage());
        }
    }

    private void send(WebSocketSession session, CommandOrEvent message) {
        try {
            var textMessage = new TextMessage(serialize(message));
            session.sendMessage(textMessage);
        } catch (IOException ex) {
            log.error("Failed to send message to WebSocket session: {}", session.getId(), ex);
        }
    }

    private String getAppName() {
        var applicationName = context.getApplicationName();
        if (!applicationName.isBlank()) return applicationName;
        return context.getId();
    }
}
