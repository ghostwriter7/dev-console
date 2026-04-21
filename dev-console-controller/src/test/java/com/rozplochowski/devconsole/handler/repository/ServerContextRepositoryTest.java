package com.rozplochowski.devconsole.handler.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.rozplochowski.devconsole.handler.ServerContext;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerContextRepositoryTest {

    private ServerContextRepository uut;

    @BeforeEach
    void setUp() {
        uut = new ServerContextRepository();
    }

    @Test
    void shouldPutServerContext() {
        var serviceName = "ServiceX";
        var context = createCtx();

        uut.put(serviceName, context);

        var result = uut.get(serviceName);
        assertTrue(result.isPresent());
        assertEquals(context, result.get());
    }

    @Test
    void shouldReturnEmptyIfServerContextDoesNotExist() {
        var result = uut.get("ServiceX");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnServerContextIfItExists() {
        var serviceName = "ServiceX";
        var context = createCtx();
        uut.put(serviceName, context);

        var result = uut.get(serviceName);

        assertTrue(result.isPresent());
        assertEquals(context, result.get());
    }

    @Test
    void shouldReturnNoEntriesWhenThereAreNoServiceContexts() {
        var entries = uut.entries();

        assertTrue(entries.isEmpty());
    }

    @Test
    void shouldReturnAllServiceContexts() {
        var serviceName1 = "ServiceX";
        var serviceName2 = "ServiceY";
        var context1 = createCtx();
        var context2 = createCtx();
        uut.put(serviceName1, context1);
        uut.put(serviceName2, context2);

        var entries = uut.entries();

        assertEquals(2, entries.size());
        assertTrue(entries.contains(Map.entry(serviceName1, context1)));
        assertTrue(entries.contains(Map.entry(serviceName2, context2)));
    }

    private ServerContext createCtx() {
        return new ServerContext(List.of(), List.of());
    }
}