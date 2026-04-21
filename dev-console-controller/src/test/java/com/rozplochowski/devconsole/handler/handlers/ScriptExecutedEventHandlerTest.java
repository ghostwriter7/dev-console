package com.rozplochowski.devconsole.handler.handlers;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.ScriptResult;
import com.rozplochowski.devconsole.SessionAttribute;
import com.rozplochowski.devconsole.common.MessageSender;
import com.rozplochowski.devconsole.handler.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScriptExecutedEventHandlerTest {

    @Mock
    private MessageSender sender;

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private ScriptExecutedEventHandler uut;

    private WebSocketSession session;
    private final CommandOrEvent.ScriptExecutedEvent event = new CommandOrEvent.ScriptExecutedEvent(
            UUID.randomUUID(),
            new ScriptResult.Success("123", "Hello world!")
    );

    @BeforeEach
    void setUp() {
        session = mock(WebSocketSession.class);
        when(session.getAttributes()).thenReturn(Map.of(SessionAttribute.SERVICE_NAME.name(), "ServiceX"));
    }

    @Test
    void shouldDoNothingWhenClientSessionIsNotPresent() {
        when(sessionRepository.tryGetClient()).thenReturn(Optional.empty());

        uut.handle(event, session);

        verifyNoInteractions(sender);
    }

    @Test
    void shouldReplyToClient() {
        var clientSession = mock(WebSocketSession.class);
        when(sessionRepository.tryGetClient()).thenReturn(Optional.of(clientSession));

        uut.handle(event, session);

        verify(sender).send(clientSession, event);
    }
}