package com.rozplochowski.devconsole.handler;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.UiClientToken;
import com.rozplochowski.devconsole.common.MessageSender;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AvailableServicesChangedNotifier {
    private final SessionRepository sessionRepository;
    private final MessageSender messageSender;

    public void notifyClient() {
        sessionRepository.tryGetClient().ifPresent(this::notifyClient);
    }

    public void notifyClient(WebSocketSession client) {
        var availableServices = sessionRepository.services();
        availableServices.remove(UiClientToken.VALUE);
        var payload = new CommandOrEvent.AvailableServicesChangedEvent(availableServices);
        messageSender.send(client, payload);
    }
}
