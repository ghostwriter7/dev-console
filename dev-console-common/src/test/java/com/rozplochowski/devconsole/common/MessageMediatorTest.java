package com.rozplochowski.devconsole.common;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.Handles;
import com.rozplochowski.devconsole.MessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageMediatorTest {

    private MessageMediator messageMediator;

    @Spy
    private MessageHandler<CommandOrEvent.ComputeTypeMetadataCommand> handlerA = new ComputeTypeMetadataHandler();
    @Spy
    private MessageHandler<CommandOrEvent.ServerConnectCommand> handlerB = new ServerConnectedHandler();
    private MessageHandler<CommandOrEvent.ExecuteScriptCommand> handlerC = new UnannotatedInvalidHandler();

    @Handles(CommandOrEvent.ServerConnectCommand.class)
    static class ServerConnectedHandler implements MessageHandler<CommandOrEvent.ServerConnectCommand> {
        @Override
        public void handle(CommandOrEvent.ServerConnectCommand message, WebSocketSession session) {
        }
    }

    @Handles(CommandOrEvent.ComputeTypeMetadataCommand.class)
    static class ComputeTypeMetadataHandler implements MessageHandler<CommandOrEvent.ComputeTypeMetadataCommand> {
        @Override
        public void handle(CommandOrEvent.ComputeTypeMetadataCommand message, WebSocketSession session) {
        }
    }

    static class UnannotatedInvalidHandler implements MessageHandler<CommandOrEvent.ExecuteScriptCommand> {
        @Override
        public void handle(CommandOrEvent.ExecuteScriptCommand message, WebSocketSession session) {
        }
    }

    @BeforeEach
    void setUp() {
        List<MessageHandler<?>> handlers = List.of(
                handlerA,
                handlerB
        );

        messageMediator = new MessageMediator(handlers);
    }

    @Test
    void shouldDispatchAMessageToAppropriateHandler() {
        var message = new CommandOrEvent.ServerConnectCommand("AnyService", List.of(), List.of());
        var session = mock(WebSocketSession.class);

        messageMediator.dispatch(session, message);

        verify(handlerB).handle(message, session);
        verify(handlerA, never()).handle(any(), any());
    }

    @Test
    void shouldThrowExceptionIfThereIsNoHandlerForMessage() {
        var message = new CommandOrEvent.ClientConnectCommand();
        var session = mock(WebSocketSession.class);

        assertThrows(IllegalArgumentException.class, () -> messageMediator.dispatch(session, message), () -> "No handler for message type: " + message.getClass());
    }

    @Test
    void shouldFailInitializationIfAHandlerDoesNotHaveAValidAnnotation() {
        List<MessageHandler<?>> handlers = List.of(handlerA, handlerB, handlerC);

        assertThrows(IllegalArgumentException.class, () -> new MessageMediator(handlers), () -> "Handler " + handlerC.getClass() + " is missing @Handles annotation");
    }

}