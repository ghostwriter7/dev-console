package com.rozplochowski.devconsole.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class DevConsoleConnector {

    private final TargetServiceWebSocketHandler handler;
    private final StandardWebSocketClient client = new StandardWebSocketClient();
    private final ThreadPoolTaskScheduler executor;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("DevConsole WebSocket connector initialized");

        connectWithRetry().whenComplete((session, throwable) -> {
            if (throwable != null) {
                log.error("Failed to connect to DevConsole WebSocket", throwable);
            } else {
                log.info("Successfully connected to DevConsole WebSocket, session ID: {}", session.getId());
            }
        });
    }

    private CompletableFuture<WebSocketSession> connectWithRetry() {
        return attemptConnect(1, 5, Duration.ofSeconds(1));
    }

    private CompletableFuture<WebSocketSession> attemptConnect(int attempt, int maxAttempts, Duration initialBackOff) {
        log.info("Attempting to connect to DevConsole WebSocket (attempt {}/{})", attempt, maxAttempts);

        return connectOnce()
                .exceptionallyCompose(throwable -> {
                    if (attempt >= maxAttempts) {
                        return CompletableFuture.failedFuture(throwable);
                    }

                    long delayInMs = initialBackOff.toMillis() * attempt;
                    log.warn("Connection attempt {} failed, retrying in {}ms", attempt, delayInMs);

                    CompletableFuture<WebSocketSession> delayedAttempt = new CompletableFuture<>();
                    executor.schedule(() -> attemptConnect(attempt + 1, maxAttempts, initialBackOff)
                            .whenComplete((s, t) -> {
                                if (t != null) delayedAttempt.completeExceptionally(t);
                                else delayedAttempt.complete(s);
                            }), Instant.now().plusMillis(delayInMs));
                    return delayedAttempt;
                });
    }

    private CompletableFuture<WebSocketSession> connectOnce() {
        return client.execute(
                handler,
                new WebSocketHttpHeaders(),
                URI.create("ws://localhost:11111/v1/dev/console/ws")
        );
    }
}
