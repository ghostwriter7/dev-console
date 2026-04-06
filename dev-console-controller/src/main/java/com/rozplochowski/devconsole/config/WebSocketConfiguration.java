package com.rozplochowski.devconsole.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.rozplochowski.devconsole.handler.DevConsoleMessageHandler;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class WebSocketConfiguration implements WebSocketConfigurer {

    private final DevConsoleMessageHandler messageHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageHandler, "/v1/dev/console/ws")
                .setAllowedOrigins("*");
    }
}
