package com.rozplochowski.devconsole.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rozplochowski.devconsole.SessionAttribute;
import com.rozplochowski.devconsole.Symbol;
import com.rozplochowski.devconsole.TypeMetadata;
import groovy.lang.Binding;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionInitializerTest {

    @Mock
    private ApplicationContext context;

    @Mock
    private Environment environment;

    @Spy
    private ObjectMapper mapper = new ObjectMapper();

    @Mock
    private AppNameProvider appNameProvider;

    @Mock
    private MetadataProvider metadataProvider;

    @InjectMocks
    private SessionInitializer uut;

    private final String appName = "AnyName";

    @BeforeEach
    void setUp() {
        when(appNameProvider.provide()).thenReturn(appName);
    }

    @Test
    void shouldInitializeGroovyShellAndDispatchServerConnectCommand() throws IOException {
        var session = mock(WebSocketSession.class);
        when(session.getAttributes()).thenReturn(new HashMap<>());
        var beanName = "AnyBeanName";
        var bean = new Object();
        var metadataBundle = new MetadataProvider.MetadataBundle(
                List.of(new TypeMetadata("String", "java.lang.String", List.of())),
                List.of(new Symbol("anyBeanName", new TypeMetadata.TypeInfo(beanName, "com.domain.AnyBeanName"))),
                List.of(new MetadataProvider.SpringNameAndBeanPair(beanName, bean))
        );
        when(metadataProvider.provide(anySet())).thenReturn(metadataBundle);

        uut.initialize(session);

        var captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(captor.capture());
        var value = captor.getValue().getPayload();

        assertEquals("""
                {"type":"serverConnectCommand","serviceName":"AnyName","typeMetadata":[{"type":"String","fullName":"java.lang.String","methods":[]}],"symbols":[{"name":"anyBeanName","type":{"type":"AnyBeanName","fullName":"com.domain.AnyBeanName"}}]}""", value);

        var bindings = (Binding) session.getAttributes().get(SessionAttribute.BINDING.name());
        assertEquals(bean, bindings.getVariable(beanName));
        assertEquals(session, bindings.getVariable(SessionAttribute.SESSION.name()));
        assertEquals(context, bindings.getVariable(SessionAttribute.CTX.name()));
        assertEquals(environment, bindings.getVariable(SessionAttribute.ENV.name()));
    }
}