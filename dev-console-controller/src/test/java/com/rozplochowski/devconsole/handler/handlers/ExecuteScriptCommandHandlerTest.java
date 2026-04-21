package com.rozplochowski.devconsole.handler.handlers;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.common.MessageSender;
import com.rozplochowski.devconsole.handler.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecuteScriptCommandHandlerTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private MessageSender sender;

    @InjectMocks
    private ExecuteScriptCommandHandler uut;

    private WebSocketSession session;

    private final CommandOrEvent.ExecuteScriptCommand cmd = new CommandOrEvent.ExecuteScriptCommand(
            UUID.randomUUID(),
            "ServiceX",
            "println 'Hello world!'"
    );

    @BeforeEach
    void setUp() {
        session = mock(WebSocketSession.class);
    }

    @Test
    void shouldDoNothingWhenThereIsNoSessionForTargetService() {
        when(sessionRepository.tryGet(cmd.targetServiceName())).thenReturn(Optional.empty());

        uut.handle(cmd, session);

        verifyNoInteractions(sender);
    }

    @Test
    void shouldProxyMessageToTargetService() {
        when(sessionRepository.tryGet(cmd.targetServiceName())).thenReturn(Optional.of(session));

        uut.handle(cmd, session);

        verify(sender).send(session, cmd);
    }
}