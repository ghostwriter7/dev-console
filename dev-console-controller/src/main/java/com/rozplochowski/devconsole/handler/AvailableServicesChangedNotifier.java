package com.rozplochowski.devconsole.handler;

import com.rozplochowski.devconsole.CommandOrEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AvailableServicesChangedNotifier {
    private final SessionRepository sessionRepository;
    private final MessageSender messageSender;

    public void notifyClient() {
        sessionRepository.getClient().ifPresent(client -> {
            var availableServices = sessionRepository.services();
            var payload = new CommandOrEvent.AvailableServicesChangedEvent(availableServices);
            messageSender.send(client, payload);
        });
    }
}
