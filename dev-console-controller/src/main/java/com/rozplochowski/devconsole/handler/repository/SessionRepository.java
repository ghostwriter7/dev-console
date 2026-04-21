package com.rozplochowski.devconsole.handler.repository;

import com.rozplochowski.devconsole.UiClientToken;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SessionRepository {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void put(String serverName, WebSocketSession session) {
        sessions.put(serverName, session);
    }

    public void remove(String serverName) {
        sessions.remove(serverName);
    }

    public Optional<WebSocketSession> tryGet(String serverName) {
        return Optional.ofNullable(sessions.get(serverName));
    }

    public Optional<WebSocketSession> tryGetClient() {
        return Optional.ofNullable(sessions.get(UiClientToken.VALUE));
    }

    public Set<String> services() {
        return new HashSet<>(sessions.keySet());
    }
}
