package com.rozplochowski.devconsole;

import org.springframework.web.socket.WebSocketSession;

public interface MessageHandler<T extends CommandOrEvent> {
    void handle(T message, WebSocketSession session);
}
