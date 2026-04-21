package com.rozplochowski.devconsole;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SessionIdReader {
    public static String read(WebSocketSession session) {
        String serviceName = (String) session.getAttributes().get(SessionAttribute.SERVICE_NAME.name());

        if (serviceName != null) return serviceName;

        return session.getId();
    }
}
