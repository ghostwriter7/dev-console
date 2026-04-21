package com.rozplochowski.devconsole.handler.handlers;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.SessionAttribute;
import com.rozplochowski.devconsole.common.MessageSender;
import com.rozplochowski.devconsole.handler.AvailableServicesChangedNotifier;
import com.rozplochowski.devconsole.handler.ServerContext;
import com.rozplochowski.devconsole.handler.repository.ServerContextRepository;
import com.rozplochowski.devconsole.handler.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServerConnectCommandHandlerTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private ServerContextRepository serverContextRepository;

    @Mock
    private MessageSender sender;

    @Mock
    private AvailableServicesChangedNotifier availableServicesChangedNotifier;

    @InjectMocks
    private ServerConnectCommandHandler uut;

    private WebSocketSession session;

    private final CommandOrEvent.ServerConnectCommand cmd = new CommandOrEvent.ServerConnectCommand(
            "ServiceX", List.of(), List.of()
    );

    @BeforeEach
    void setUp() {
        session = mock(WebSocketSession.class);
        when(session.getAttributes()).thenReturn(new HashMap<>());
    }

    @Test
    void shouldAcknowledgeServerConnectionAndDoNothingElseWhenClientSessionIsNotPresent() {
        when(sessionRepository.tryGetClient()).thenReturn(Optional.empty());

        uut.handle(cmd, session);

        verify(sessionRepository).put(cmd.serviceName(), session);
        assertEquals(cmd.serviceName(), session.getAttributes().get(SessionAttribute.SERVICE_NAME.name()));
        verifyNoInteractions(availableServicesChangedNotifier);
        verify(sender).send(session, new CommandOrEvent.ServerConnectedEvent(cmd.serviceName()));
        verify(serverContextRepository).put(cmd.serviceName(), new ServerContext(cmd.typeMetadata(), cmd.symbols()));
    }

    @Test
    void shouldAcknowledgeServerConnectionAndNotifyClientSessionIsNotPresent() {
        var clientSession = mock(WebSocketSession.class);
        when(sessionRepository.tryGetClient()).thenReturn(Optional.of(clientSession));

        uut.handle(cmd, session);

        verify(sessionRepository).put(cmd.serviceName(), session);
        assertEquals(cmd.serviceName(), session.getAttributes().get(SessionAttribute.SERVICE_NAME.name()));
        verify(availableServicesChangedNotifier).notifyClient(clientSession);
        verify(sender).send(session, new CommandOrEvent.ServerConnectedEvent(cmd.serviceName()));
        verify(sender).send(clientSession, new CommandOrEvent.InstallTypeMetadataCommand(cmd.serviceName(), cmd.typeMetadata(), cmd.symbols()));
        verify(serverContextRepository).put(cmd.serviceName(), new ServerContext(cmd.typeMetadata(), cmd.symbols()));
    }
}