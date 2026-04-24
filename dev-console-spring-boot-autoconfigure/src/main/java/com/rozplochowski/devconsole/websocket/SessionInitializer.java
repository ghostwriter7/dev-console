package com.rozplochowski.devconsole.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rozplochowski.devconsole.CommandOrEvent;
import groovy.lang.Binding;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;

import static com.rozplochowski.devconsole.SessionAttribute.*;
import static com.rozplochowski.devconsole.SessionAttribute.BINDING;
import static com.rozplochowski.devconsole.SessionAttribute.SHELL;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class SessionInitializer {
    private final ApplicationContext context;
    private final Environment environment;
    private final ObjectMapper mapper;
    private final MetadataProvider metadataProvider;
    private final AppNameProvider appNameProvider;

    private final Set<String> excludedNames = Set.of("springframework", "jpa", "Controller");

    void initialize(WebSocketSession session) {
        log.info("New DevConsole WebSocket connection established.");

        MetadataProvider.MetadataBundle metadataBundle = metadataProvider.provide(excludedNames);
        initializeGroovyShell(session, metadataBundle);
        sendServerConnectCommand(session, metadataBundle);
    }

    private void initializeGroovyShell(WebSocketSession session, MetadataProvider.MetadataBundle metadataBundle) {
        Binding binding = createBinding(session);
        var attributes = session.getAttributes();
        attributes.put(BINDING.name(), binding);

        for (var beanPair : metadataBundle.beanPairs()) {
            binding.setVariable(beanPair.name(), beanPair.bean());
        }

        var shell = GroovyShellFactory.fromBinding(binding);
        attributes.put(SHELL.name(), shell);
    }

    private void sendServerConnectCommand(WebSocketSession session, MetadataProvider.MetadataBundle metadataBundle) {
        var appName = appNameProvider.provide();
        log.info("Sending initial WebSocket message for app: {}", appName);
        var payload = new CommandOrEvent.ServerConnectCommand(appName, metadataBundle.typeMetadataList(), metadataBundle.symbols());

        try {
            var message = mapper.writeValueAsString(payload);
            session.sendMessage(new TextMessage(message));
        } catch (JsonProcessingException e) {
            log.error("Could not serialize ServerConnectCommand: {}", e.getMessage());
        } catch (IOException e) {
            log.error("Could not send ServerConnectCommand: {}", e.getMessage());
        }
    }

    private Binding createBinding(WebSocketSession session) {
        var vars = new HashMap<String, Object>();
        vars.put(CTX.name(), context);
        vars.put(ENV.name(), environment);
        vars.put(SESSION.name(), session);
        return new Binding(vars);
    }
}
