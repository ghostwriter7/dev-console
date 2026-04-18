package com.rozplochowski.devconsole.common;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.Handles;
import com.rozplochowski.devconsole.MessageHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MessageMediator {

    private final Map<Class<? extends CommandOrEvent>, MessageHandler<?>> handlers;

    MessageMediator(List<MessageHandler<?>> handlers) {
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(handler -> {
                    var handles = handler.getClass().getAnnotation(Handles.class);
                    if (handles == null) {
                        throw new IllegalArgumentException("Handler " + handler.getClass() + " is missing @Handles annotation");
                    }
                    return handles.value();
                }, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <T extends CommandOrEvent> void dispatch(WebSocketSession session, T commandOrEvent) {
        var handler = (MessageHandler<T>) handlers.get(commandOrEvent.getClass());

        if (handler == null) {
            throw new IllegalArgumentException("No handler for message type: " + commandOrEvent.getClass());
        }

        handler.handle(commandOrEvent, session);
    }
}
