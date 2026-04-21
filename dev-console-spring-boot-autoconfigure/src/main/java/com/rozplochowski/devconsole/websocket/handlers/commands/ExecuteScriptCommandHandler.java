package com.rozplochowski.devconsole.websocket.handlers.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rozplochowski.devconsole.*;
import com.rozplochowski.devconsole.common.MessageSender;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.rozplochowski.devconsole.SessionAttribute.*;

@Slf4j
@Component
@Handles(CommandOrEvent.ExecuteScriptCommand.class)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ExecuteScriptCommandHandler implements MessageHandler<CommandOrEvent.ExecuteScriptCommand> {
    private final ObjectMapper mapper;
    private final MessageSender sender;

    @Override
    public void handle(CommandOrEvent.ExecuteScriptCommand command, @NonNull WebSocketSession session) {
        var attributes = session.getAttributes();
        Binding binding = (Binding) attributes.get(BINDING.name());
        GroovyShell shell = (GroovyShell) attributes.get(SHELL.name());

        var output = new StringWriter();
        var writer = new PrintWriter(output);
        binding.setProperty("out", writer);

        log.info("Executing Script for Command UUID: {}: {} ", command.uuid(), command.script());

        try {
            var result = shell.evaluate(command.script());

            var jsonResult = result == null ? null : mapper.writeValueAsString(result);
            var scriptResult = new ScriptResult.Success(jsonResult, output.toString());
            var message = new CommandOrEvent.ScriptExecutedEvent(command.uuid(), scriptResult);

            log.info("Responding with a success: {}", message);

            sender.send(session, message);
        } catch (Exception e) {

            var stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));

            var scriptResult = new ScriptResult.Error(e.getMessage(), stringWriter.toString());
            var message = new CommandOrEvent.ScriptExecutedEvent(command.uuid(), scriptResult);

            log.info("Responding with an error: {}", message);
            sender.send(session, message);
        }
    }

}
