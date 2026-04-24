package com.rozplochowski.devconsole.websocket.handlers.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.ScriptResult;
import com.rozplochowski.devconsole.SessionAttribute;
import com.rozplochowski.devconsole.common.MessageSender;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecuteScriptCommandHandlerTest {
    @Spy
    private ObjectMapper mapper = new ObjectMapper();

    @Mock
    private MessageSender sender;

    @InjectMocks
    private ExecuteScriptCommandHandler uut;

    @Test
    void shouldExecuteScript() {
        var session = mock(WebSocketSession.class);
        var binding = mock(Binding.class);
        var shell = mock(GroovyShell.class);
        var attributes = new HashMap<String, Object>(
                Map.of(
                        SessionAttribute.BINDING.name(), binding,
                        SessionAttribute.SHELL.name(), shell
                )
        );
        when(session.getAttributes()).thenReturn(attributes);
        var command = new CommandOrEvent.ExecuteScriptCommand(
                UUID.randomUUID(), "ServiceX", "['Hello', 'world!']"
        );
        when(shell.evaluate(command.script())).thenReturn(new String[]{"Hello", "world!"});

        uut.handle(command, session);

        verify(sender).send(session, new CommandOrEvent.ScriptExecutedEvent(
                command.uuid(),
                new ScriptResult.Success("[\"Hello\",\"world!\"]", "")
        ));
    }

    @Test
    void shouldHandleScriptExecutionFailure() {
        var session = mock(WebSocketSession.class);
        var binding = mock(Binding.class);
        var shell = mock(GroovyShell.class);
        var attributes = new HashMap<String, Object>(
                Map.of(
                        SessionAttribute.BINDING.name(), binding,
                        SessionAttribute.SHELL.name(), shell
                )
        );
        when(session.getAttributes()).thenReturn(attributes);
        var command = new CommandOrEvent.ExecuteScriptCommand(
                UUID.randomUUID(), "ServiceX", "throw new IllegalArgumentException('Test')"
        );
        when(shell.evaluate(command.script())).thenThrow(new IllegalArgumentException("Test"));

        uut.handle(command, session);

        var argCaptor = ArgumentCaptor.forClass(CommandOrEvent.ScriptExecutedEvent.class);
        verify(sender).send(eq(session), argCaptor.capture());
        var event = argCaptor.getValue();
        assertEquals(command.uuid(), event.uuid());
        assertInstanceOf(ScriptResult.Error.class, event.scriptResult());
        var result = (ScriptResult.Error) event.scriptResult();
        assertEquals("Test", result.error());
        assertTrue(result.stackTrace().startsWith("java.lang.IllegalArgumentException: Test"));
    }
}