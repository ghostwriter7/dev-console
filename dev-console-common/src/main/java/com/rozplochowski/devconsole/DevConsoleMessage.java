package com.rozplochowski.devconsole;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;
import java.util.UUID;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = DevConsoleMessage.ServerConnectCommand.class, name = "serverConnectCommand"),
        @JsonSubTypes.Type(value = DevConsoleMessage.ExecuteScriptCommand.class, name = "executeScriptCommand"),
        @JsonSubTypes.Type(value = DevConsoleMessage.ScriptExecutedEvent.class, name = "scriptExecutedEvent")
})
public sealed interface DevConsoleMessage {
    record ServerConnectCommand(String serviceName) implements DevConsoleMessage {
    }

    record ExecuteScriptCommand(UUID uuid, String targetServiceName, String script) implements DevConsoleMessage {
    }

    record ScriptExecutedEvent(UUID uuid, Map<String, Object> result) implements DevConsoleMessage {
    }
}
