package com.rozplochowski.devconsole.handler.handlers;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.SessionAttribute;
import com.rozplochowski.devconsole.UiClientToken;
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
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientConnectCommandHandlerTest {

    @Mock
    private ServerContextRepository serverContextRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private MessageSender sender;

    @Mock
    private AvailableServicesChangedNotifier availableServicesChangedNotifier;

    @InjectMocks
    private ClientConnectCommandHandler uut;

    private WebSocketSession session;

    @BeforeEach
    void setUp() {
        session = mock(WebSocketSession.class);
        when(session.getAttributes()).thenReturn(new HashMap<>());
    }

    @Test
    void shouldHandleClientConnectionAndNotSendCommandsWhenThereAreNoConnectedServices() {
        when(serverContextRepository.entries()).thenReturn(Set.of());
        var cmd = new CommandOrEvent.ClientConnectCommand();

        uut.handle(cmd, session);

        verify(sessionRepository).put(UiClientToken.VALUE, session);
        verify(availableServicesChangedNotifier).notifyClient(session);
        assertEquals(UiClientToken.VALUE, session.getAttributes().get(SessionAttribute.SERVICE_NAME.name()));
        verifyNoInteractions(sender);
    }

    @Test
    void shouldHandleClientConnectionAndSendCommandsWhenThereAreConnectedServices() {
        var serverAContext = new ServerContext(List.of(), List.of());
        var serverBContext = new ServerContext(List.of(), List.of());
        when(serverContextRepository.entries()).thenReturn(Set.of(
                 Map.entry("ServerA", serverAContext),
                 Map.entry("ServerB", serverBContext)
        ));
        var cmd = new CommandOrEvent.ClientConnectCommand();

        uut.handle(cmd, session);

        verify(sessionRepository).put(UiClientToken.VALUE, session);
        verify(availableServicesChangedNotifier).notifyClient(session);
        assertEquals(UiClientToken.VALUE, session.getAttributes().get(SessionAttribute.SERVICE_NAME.name()));
        verify(sender).send(session, new CommandOrEvent.InstallTypeMetadataCommand("ServerA", serverAContext.typeMetadata(), serverAContext.symbols()));
        verify(sender).send(session, new CommandOrEvent.InstallTypeMetadataCommand("ServerB", serverBContext.typeMetadata(), serverBContext.symbols()));
    }
}