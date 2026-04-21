package com.rozplochowski.devconsole.handler;

import com.rozplochowski.devconsole.UiClientToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SessionRepositoryTest {

    private SessionRepository sessionRepository;
    private WebSocketSession session;

    private static final String ANY_NAME = "AnyName";

    @BeforeEach
    void setUp() {
        sessionRepository = new SessionRepository();
        session = mock(WebSocketSession.class);
    }

    @Test
    void shouldPutSession() {
        sessionRepository.put(ANY_NAME, session);

        var result = sessionRepository.tryGet(ANY_NAME);
        assertTrue(result.isPresent());
        assertEquals(session, result.get());
    }

    @Test
    void shouldRemoveSession() {
        sessionRepository.put(ANY_NAME, session);

        sessionRepository.remove(ANY_NAME);

        var result = sessionRepository.tryGet(ANY_NAME);
        assertFalse(result.isPresent());
    }

    @Test
    void shouldTryGetSessionAndReturnEmptyIfSessionDoesNotExist() {
        sessionRepository.put(ANY_NAME, session);
        sessionRepository.put("yetAnotherName", mock(WebSocketSession.class));

        var result = sessionRepository.tryGet("differentName");

        assertFalse(result.isPresent());
    }

    @Test
    void shouldTryGetSessionAndReturnSessionIfSessionExists() {
        sessionRepository.put("differentName", mock(WebSocketSession.class));
        sessionRepository.put("yetAnotherName", mock(WebSocketSession.class));
        sessionRepository.put(ANY_NAME, session);

        var result = sessionRepository.tryGet(ANY_NAME);

        assertTrue(result.isPresent());
        assertEquals(session, result.get());
    }

    @Test
    void shouldTryGetClientSessionAndReturnSessionIfClientSessionExists() {
        sessionRepository.put(UiClientToken.VALUE, session);

        var result = sessionRepository.tryGetClient();

        assertTrue(result.isPresent());
        assertEquals(session, result.get());
    }

    @Test
    void shouldTryGetClientSessionAndReturnEmptyIfClientSessionDoesNotExist() {
        var result = sessionRepository.tryGetClient();

        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnAllRegisteredServiceNames() {
        sessionRepository.put("differentName", mock(WebSocketSession.class));
        sessionRepository.put("yetAnotherName", mock(WebSocketSession.class));
        sessionRepository.put(ANY_NAME, session);

        var result = sessionRepository.services();

        assertEquals(3, result.size());
        assertTrue(result.contains("differentName"));
        assertTrue(result.contains(ANY_NAME));
        assertTrue(result.contains("yetAnotherName"));
    }
}