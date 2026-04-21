package com.rozplochowski.devconsole.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.ScriptResult;
import com.rozplochowski.devconsole.SessionAttribute;
import com.rozplochowski.devconsole.UiClientToken;
import com.rozplochowski.devconsole.common.MessageMediator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import com.rozplochowski.devconsole.handler.repository.SessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DevConsoleMessageHandlerTest {

    @Mock
    private SessionRepository sessionRepository;

    @Spy
    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private MessageMediator messageMediator;

    @Mock
    private AvailableServicesChangedNotifier availableServicesChangedNotifier;

    @InjectMocks
    private DevConsoleMessageHandler uut;

    @Nested
    class AfterConnectionClosed {

        @Test
        void shouldDoNothingWhenThereIsNoServerNameInSessionAttributes() {
            var session = createSession(Map.of());

            uut.afterConnectionClosed(session, CloseStatus.GOING_AWAY);

            verifyNoInteractions(sessionRepository);
            verifyNoInteractions(availableServicesChangedNotifier);
        }

        @Test
        void shouldRemoveSessionFromRepositoryAndNotifyAvailableServicesChanged() {
            var session = createSession(Map.of(SessionAttribute.SERVICE_NAME.name(), "ServerX"));

            uut.afterConnectionClosed(session, CloseStatus.GOING_AWAY);

            verify(sessionRepository).remove("ServerX");
            verify(availableServicesChangedNotifier).notifyClient();
        }

        @Test
        void shouldRemoveSessionFromRepositoryOnlyWhenClientDisconnected() {
            var session = createSession(Map.of(SessionAttribute.SERVICE_NAME.name(), UiClientToken.VALUE));

            uut.afterConnectionClosed(session, CloseStatus.SERVER_ERROR);

            verify(sessionRepository).remove(UiClientToken.VALUE);
            verify(availableServicesChangedNotifier, never()).notifyClient();
        }
    }

    @Nested
    class handleMessage {
        @Test
        void shouldDispatchMessageToMediator() throws JsonProcessingException {
            var session = createSession(Map.of(SessionAttribute.SERVICE_NAME.name(), "ServerX"));
            var command = new CommandOrEvent.ScriptExecutedEvent(UUID.randomUUID(), new ScriptResult.Success("123", "Hello world!"));
            var message = mock(WebSocketMessage.class);
            var json = mapper.writeValueAsString(command);
            when(message.getPayload()).thenReturn(json);

            uut.handleMessage(session, message);

            verify(messageMediator).dispatch(session, command);
        }
    }

    private WebSocketSession createSession(Map<String, Object> attributes) {
        var session = mock(WebSocketSession.class);
        when(session.getAttributes()).thenReturn(attributes);
        return session;
    }
}