package com.rozplochowski.devconsole.autoconfigure;

import com.rozplochowski.devconsole.websocket.DevConsoleWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@RequiredArgsConstructor
public class DevConsoleWebSocketConfigurer implements WebSocketConfigurer {

    private final DevConsoleWebSocketHandler devConsoleWebSocketHandler;
    private final DevConsoleProperties properties;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(devConsoleWebSocketHandler, properties.getPath())
                .setAllowedOrigins(properties.getAllowedOrigins().toArray(new String[0]));
    }
}
