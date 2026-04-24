package com.rozplochowski.devconsole.websocket;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class AppNameProvider {

    private final ApplicationContext context;

    String provide() {
        var applicationName = context.getApplicationName();
        if (!applicationName.isBlank()) return applicationName;
        return context.getId();
    }
}
