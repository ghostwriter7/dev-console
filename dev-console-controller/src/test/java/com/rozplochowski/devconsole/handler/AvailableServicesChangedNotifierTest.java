package com.rozplochowski.devconsole.handler;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.UiClientToken;
import com.rozplochowski.devconsole.common.MessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailableServicesChangedNotifierTest {

    @Mock
    private com.rozplochowski.devconsole.handler.repository.SessionRepository sessionRepository;

    @Mock
    private MessageSender messageSender;

    @InjectMocks
    private AvailableServicesChangedNotifier uut;

    private final Set<String> services = Set.of("service1", "service2", UiClientToken.VALUE);

    @Test
    void shouldDoNothingWhenClientSessionIsNotAvailable() {
        when(sessionRepository.tryGetClient()).thenReturn(Optional.empty());

        uut.notifyClient();

        verify(sessionRepository, never()).services();
        verifyNoInteractions(messageSender);
    }

    @Test
    void shouldNotifyClientWhenSessionIsAvailable() {
        var clientSession = mock(WebSocketSession.class);
        when(sessionRepository.tryGetClient()).thenReturn(Optional.of(clientSession));
        when(sessionRepository.services()).thenReturn(new HashSet<>(services));

        uut.notifyClient();

        verify(messageSender).send(clientSession, new CommandOrEvent.AvailableServicesChangedEvent(Set.of("service1", "service2")));
    }

    @Test
    void shouldNotifyClientWhenSessionIsProvidedByCaller() {
        var clientSession = mock(WebSocketSession.class);
        when(sessionRepository.services()).thenReturn(new HashSet<>(services));

        uut.notifyClient(clientSession);

        verify(messageSender).send(clientSession, new CommandOrEvent.AvailableServicesChangedEvent(Set.of("service1", "service2")));
    }
}