package com.rozplochowski.devconsole.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rozplochowski.devconsole.CommandOrEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MessageSender {

    private final ObjectMapper mapper;

    public boolean send(WebSocketSession session, CommandOrEvent message) {
        try {
            var textMessage = new TextMessage(serialize(message));
            session.sendMessage(textMessage);
            return true;
        } catch (IOException ex) {
            log.error("Failed to send message to WebSocket session: {}", session.getId(), ex);
            return false;
        }
    }

    private String serialize(CommandOrEvent message) {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not serialize JSON: " + e.getMessage());
        }
    }
}


