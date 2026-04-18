package com.rozplochowski.devconsole.common;

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
        var serviceName = session.getAttributes().get("serviceName");
        var sessionId = serviceName == null ? session.getId() : serviceName;
        try {
            var textMessage = new TextMessage(serialize(message));
            session.sendMessage(textMessage);
            log.info("Sent message to: '{}' of type: {}", sessionId, message.getClass().getSimpleName());
            return true;
        } catch (IOException ex) {
            log.error("Failed to send message to: '{}' of type: {}", sessionId, message.getClass().getSimpleName(), ex);
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


