package com.rozplochowski.devconsole.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rozplochowski.devconsole.DevConsoleMessage;
import com.rozplochowski.devconsole.autoconfigure.DevConsoleProperties;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class DevConsoleWebSocketHandler implements WebSocketHandler {

    private final ApplicationContext context;
    private final Environment environment;
    private final DevConsoleProperties properties;
    private final ObjectMapper mapper;

    @Value("${spring.application.name:UnknownAppName}")
    private final String appName;

    private static final String CTX = "ctx";
    private static final String ENV = "env";
    private static final String SESSION = "session";
    private static final String BINDING = "binding";
    private static final String SHELL = "shell";

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        log.info("New DevConsole WebSocket connection established.");

        Map<String, Object> vars = new HashMap<>();
        vars.put(CTX, context);
        vars.put(ENV, environment);
        vars.put(SESSION, session);

        Binding binding = new Binding(vars);
        session.getAttributes().put(BINDING, binding);

        var importCustomizer = new ImportCustomizer();
        importCustomizer.addImports("groovy.json.JsonOutput");
        importCustomizer.addStarImports("java.util", "java.time");

        for (var name : context.getBeanDefinitionNames()) {
            if (!name.contains("springframework") && !name.contains("jpa") && !name.endsWith("Controller")) {
                vars.put(name, context.getBean(name));
            }
        }

        var compilerConfig = new CompilerConfiguration();
        compilerConfig.addCompilationCustomizers(importCustomizer);

        var shell = new GroovyShell(getClass().getClassLoader(), binding, compilerConfig);
        session.getAttributes().put(SHELL, shell);

        log.info("Sending initial WebSocket message for app: {}", appName);
        var payload = new DevConsoleMessage.ServerConnectCommand(appName);
        var message = mapper.writeValueAsString(payload);

        session.sendMessage(new TextMessage(message));    }

    @Override
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws Exception {
        log.info("Received message: {}", message.getPayload());

        if (message.getPayload() instanceof String script) {
            var attributes = session.getAttributes();
            Binding binding = (Binding) attributes.get(BINDING);
            GroovyShell shell = (GroovyShell) attributes.get(SHELL);

            var output = new StringWriter();
            var writer = new PrintWriter(output);
            binding.setProperty("out", writer);

            try {
                var result = shell.evaluate(script);
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("result", result == null ? null : mapper.writeValueAsString(result));
                response.put("output", output.toString());

                log.info("Responding with a success: {}", response);

                session.sendMessage(new TextMessage(mapper.writeValueAsString(response)));
            } catch (Exception e) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", e.getMessage());

                var stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                response.put("stackTrace", stringWriter.toString());
                response.put("status", "error");

                log.info("Responding with an error: {}", response);
                session.sendMessage(new TextMessage(mapper.writeValueAsString(response)));
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) {
        log.info("DevConsole WebSocket connection closed.");
        var attributes = session.getAttributes();
        attributes.remove(BINDING);
        attributes.remove(SHELL);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
