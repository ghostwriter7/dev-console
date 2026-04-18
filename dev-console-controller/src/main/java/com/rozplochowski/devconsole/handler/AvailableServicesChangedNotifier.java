package com.rozplochowski.devconsole.handler;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.common.MessageSender;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AvailableServicesChangedNotifier {
    private final SessionRepository sessionRepository;
    private final MessageSender messageSender;

    public void notifyClient() {
        sessionRepository.getClient().ifPresent(client -> {
            var availableServices = new HashSet<>(sessionRepository.services());
            availableServices.remove("CLIENT");
            var payload = new CommandOrEvent.AvailableServicesChangedEvent(availableServices);
            messageSender.send(client, payload);
        });
    }
}
