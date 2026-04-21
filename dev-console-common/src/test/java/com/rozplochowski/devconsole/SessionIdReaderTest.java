package com.rozplochowski.devconsole;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SessionIdReaderTest {

    @Test
    void shouldReturnServiceNameAttributeWhenPresent() {
        var session = mock(WebSocketSession.class);
        when(session.getAttributes()).thenReturn(Map.of(SessionAttribute.SERVICE_NAME.name(), "AnyName"));
        when(session.getId()).thenReturn("AnyId");

        var sessionId = SessionIdReader.read(session);

        assertEquals("AnyName", sessionId);
    }

    @Test
    void shouldReturnSessionIdWhenServiceNameAttributeIsNotPresent() {
        var session = mock(WebSocketSession.class);
        when(session.getAttributes()).thenReturn(Map.of());
        when(session.getId()).thenReturn("AnyId");

        var sessionId = SessionIdReader.read(session);

        assertEquals("AnyId", sessionId);
    }

}