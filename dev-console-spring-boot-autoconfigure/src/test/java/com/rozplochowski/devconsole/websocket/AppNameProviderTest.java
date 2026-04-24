package com.rozplochowski.devconsole.websocket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppNameProviderTest {

    @Mock
    private ApplicationContext context;

    @InjectMocks
    private AppNameProvider uut;

    @Test
    void shouldUseApplicationNameFromContextIfPresent() {
        var appName = "Demo";
        when(context.getApplicationName()).thenReturn(appName);

        var result = uut.provide();

        assertEquals(appName, result);
    }

    @Test
    void shouldUseAppIdWhenApplicationNameIsNotPresent() {
        var appId = "demo-app";
        when(context.getApplicationName()).thenReturn("");
        when(context.getId()).thenReturn(appId);

        var result = uut.provide();

        assertEquals(appId, result);
    }
}