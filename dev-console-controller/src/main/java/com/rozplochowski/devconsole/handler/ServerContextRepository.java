package com.rozplochowski.devconsole.handler;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ServerContextRepository {
    private final Map<String, ServerContext> serverContextMap = new ConcurrentHashMap<>();

    public void put(String serviceName, ServerContext serverContext) {
        serverContextMap.put(serviceName, serverContext);
    }

    public Optional<ServerContext> get(String serviceName) {
        return Optional.ofNullable(serverContextMap.get(serviceName));
    }

    public Set<Map.Entry<String, ServerContext>> entries() {
        return serverContextMap.entrySet();
    }
}
