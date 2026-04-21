package com.rozplochowski.devconsole.handler.handlers;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.common.MessageSender;
import com.rozplochowski.devconsole.handler.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TypeMetadataComputedEventHandlerTest {

    @Mock
    private MessageSender sender;

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private TypeMetadataComputedEventHandler uut;

    private final CommandOrEvent.TypeMetadataComputedEvent event = new CommandOrEvent.TypeMetadataComputedEvent(null, UUID.randomUUID());

    @Test
    void shouldDoNothingWhenThereIsNoClientSession() {
        when(sessionRepository.tryGetClient()).thenReturn(Optional.empty());

        uut.handle(event, mock(WebSocketSession.class));

        verifyNoInteractions(sender);
    }

    @Test
    void shouldReplyToClient() {
        var clientSession = mock(WebSocketSession.class);
        when(sessionRepository.tryGetClient()).thenReturn(Optional.of(clientSession));

        uut.handle(event, mock(WebSocketSession.class));

        verify(sender).send(clientSession, event);
    }
}