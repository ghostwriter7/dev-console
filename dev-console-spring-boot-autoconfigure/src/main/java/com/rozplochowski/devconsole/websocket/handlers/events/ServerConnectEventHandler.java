package com.rozplochowski.devconsole.websocket.handlers.events;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.Handles;
import com.rozplochowski.devconsole.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
@Handles(CommandOrEvent.ServerConnectedEvent.class)
class ServerConnectEventHandler implements MessageHandler<CommandOrEvent.ServerConnectedEvent> {
    @Override
    public void handle(CommandOrEvent.ServerConnectedEvent message, WebSocketSession session) {
        log.info("Received acknowledgement from the server. Connection established.");
    }
}
