package com.rozplochowski.devconsole.handler.handlers;

import com.rozplochowski.devconsole.CommandOrEvent;import org.springframework.web.socket.WebSocketSession;

public interface MessageHandler<T extends CommandOrEvent> {
    void handle(T message, WebSocketSession session);
}
