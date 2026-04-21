package com.rozplochowski.devconsole.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rozplochowski.devconsole.CommandOrEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageSenderTest {

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private MessageSender messageSender;

    private WebSocketSession session;
    private CommandOrEvent command = new CommandOrEvent.ServerConnectCommand("AnyName", List.of(), List.of());


    @BeforeEach
    void setUp() {
        session = mock(WebSocketSession.class);
        when(session.getAttributes()).thenReturn(Map.of("serviceName", "AnyName"));
    }

    @Test
    void shouldSendAMessage() throws IOException {
        var json = """
                {
                    "serviceName": "AnyName",
                    "typeMetadata": [],
                    "symbols": []
                 }""";
        when(mapper.writeValueAsString(command)).thenReturn(json);

        var isSent = messageSender.send(session, command);

        var argCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(argCaptor.capture());
        assertEquals(json, argCaptor.getValue().getPayload());
        assertTrue(isSent);
    }

    @Test
    void shouldThrowAnExceptionIfPayloadCannotBeSerialized() throws JsonProcessingException {
        when(mapper.writeValueAsString(command)).thenThrow(new JsonMappingException("Serialization failed"));

        var ex = assertThrows(IllegalArgumentException.class,
                () -> messageSender.send(session, command),
                "Could not serialize JSON: Serialization failed");

        assertEquals("Could not serialize JSON: Serialization failed", ex.getMessage());
    }

    @Test
    void shouldReturnFalseIfMessageCannotBeSent() throws IOException {
        var json = """
                {
                    "serviceName": "AnyName",
                    "typeMetadata": [],
                    "symbols": []
                 }""";
        when(mapper.writeValueAsString(command)).thenReturn(json);
        doThrow(new IOException("Message sending failed")).when(session).sendMessage(any(TextMessage.class));

        var isSent = messageSender.send(session, command);

        assertFalse(isSent);
    }
}