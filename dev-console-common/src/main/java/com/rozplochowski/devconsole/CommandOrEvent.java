package com.rozplochowski.devconsole;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = CommandOrEvent.ServerConnectCommand.class, name = "serverConnectCommand"),
        @JsonSubTypes.Type(value = CommandOrEvent.ClientConnectCommand.class, name = "clientConnectCommand"),
        @JsonSubTypes.Type(value = CommandOrEvent.ExecuteScriptCommand.class, name = "executeScriptCommand"),
        @JsonSubTypes.Type(value = CommandOrEvent.InstallTypeMetadataCommand.class, name = "installTypeMetadataCommand"),
        @JsonSubTypes.Type(value = CommandOrEvent.ScriptExecutedEvent.class, name = "scriptExecutedEvent"),
        @JsonSubTypes.Type(value = CommandOrEvent.ServerConnectedEvent.class, name = "serverConnectedEvent"),
        @JsonSubTypes.Type(value = CommandOrEvent.AvailableServicesChangedEvent.class, name = "availableServicesChangedEvent")
})
public sealed interface CommandOrEvent {
    record ServerConnectCommand(String serviceName, Map<String, TypeMetadata> typeMetadata) implements CommandOrEvent {
    }

    record ClientConnectCommand() implements CommandOrEvent {}

    record ExecuteScriptCommand(UUID uuid, String targetServiceName, String script) implements CommandOrEvent {
    }

    record InstallTypeMetadataCommand(String serviceName, Map<String, TypeMetadata> typeMetadata) implements CommandOrEvent {}

    record ScriptExecutedEvent(UUID uuid, ScriptResult scriptResult) implements CommandOrEvent {
    }
    record ServerConnectedEvent(String serviceName) implements CommandOrEvent {
    }

    record AvailableServicesChangedEvent(List<String> services) implements CommandOrEvent {
    }
}
