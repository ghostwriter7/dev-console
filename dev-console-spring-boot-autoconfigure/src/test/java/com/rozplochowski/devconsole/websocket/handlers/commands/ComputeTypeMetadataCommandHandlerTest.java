package com.rozplochowski.devconsole.websocket.handlers.commands;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.common.MessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ComputeTypeMetadataCommandHandlerTest {
    @Mock
    private MessageSender sender;

    @InjectMocks
    private ComputeTypeMetadataCommandHandler uut;

    private WebSocketSession session;

    @BeforeEach
    void setUp() {
        session = mock(WebSocketSession.class);
    }

    @Test
    void shouldReturnAnEmptyResponseIfClassCannotBeFound() {
        var cmd = new CommandOrEvent.ComputeTypeMetadataCommand("ServiceX", "java.lang.NonExistingClass", UUID.randomUUID());

        uut.handle(cmd, session);

        verify(sender).send(session, new CommandOrEvent.TypeMetadataComputedEvent(null, cmd.id()));
    }

    @Test
    void shouldReturnTypeMetadataIfClassExists() {
        var cmd = new CommandOrEvent.ComputeTypeMetadataCommand("ServiceX", "java.lang.String", UUID.randomUUID());

        uut.handle(cmd, session);

        var argCaptor = ArgumentCaptor.forClass(CommandOrEvent.TypeMetadataComputedEvent    .class);
        verify(sender).send(eq(session), argCaptor.capture());
        var typeMetadata = argCaptor.getValue().typeMetadata();
        assertEquals("String", typeMetadata.type());
        assertEquals("java.lang.String", typeMetadata.fullName());
        assertFalse(typeMetadata.methods().isEmpty());
    }
}

