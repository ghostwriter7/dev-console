package com.rozplochowski.devconsole.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Component
public class DevConsoleConnector {

    private final DevConsoleWebSocketHandler handler;
    private WebSocketSession session;

    @EventListener(ApplicationReadyEvent.class)
    public void init() throws IOException, ExecutionException, InterruptedException {
        log.info("DevConsole WebSocket connector initialized");

        var client = new StandardWebSocketClient();
        session = client.execute(
                handler,
                new WebSocketHttpHeaders(),
                URI.create("ws://localhost:8080/v1/dev/console/ws")
        ).get();

        log.info("WebSocket session open: {}", session.isOpen());
    }

}
