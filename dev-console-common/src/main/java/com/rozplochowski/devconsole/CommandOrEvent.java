package com.rozplochowski.devconsole;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;
import java.util.Set;
import java.util.UUID;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = CommandOrEvent.ServerConnectCommand.class, name = "serverConnectCommand"),
        @JsonSubTypes.Type(value = CommandOrEvent.ClientConnectCommand.class, name = "clientConnectCommand"),
        @JsonSubTypes.Type(value = CommandOrEvent.ExecuteScriptCommand.class, name = "executeScriptCommand"),
        @JsonSubTypes.Type(value = CommandOrEvent.InstallTypeMetadataCommand.class, name = "installTypeMetadataCommand"),
        @JsonSubTypes.Type(value = CommandOrEvent.ComputeTypeMetadataCommand.class, name = "computeTypeMetadataCommand"),
        @JsonSubTypes.Type(value = CommandOrEvent.ScriptExecutedEvent.class, name = "scriptExecutedEvent"),
        @JsonSubTypes.Type(value = CommandOrEvent.ServerConnectedEvent.class, name = "serverConnectedEvent"),
        @JsonSubTypes.Type(value = CommandOrEvent.TypeMetadataComputedEvent.class, name = "typeMetadataComputedEvent"),
        @JsonSubTypes.Type(value = CommandOrEvent.AvailableServicesChangedEvent.class, name = "availableServicesChangedEvent")
})
public sealed interface CommandOrEvent {
    record ServerConnectCommand(String serviceName, List<TypeMetadata> typeMetadata,
                                List<Symbol> symbols) implements CommandOrEvent {
    }

    record ClientConnectCommand() implements CommandOrEvent {
    }

    record ExecuteScriptCommand(UUID uuid, String targetServiceName, String script) implements CommandOrEvent {
    }

    record InstallTypeMetadataCommand(String serviceName, List<TypeMetadata> typeMetadata,
                                      List<Symbol> symbols) implements CommandOrEvent {
    }

    record ComputeTypeMetadataCommand(String targetServiceName, String fullName, UUID id) implements CommandOrEvent {
    }

    record TypeMetadataComputedEvent(TypeMetadata typeMetadata, UUID id) implements CommandOrEvent {}

    record ScriptExecutedEvent(UUID uuid, ScriptResult scriptResult) implements CommandOrEvent {
    }

    record ServerConnectedEvent(String serviceName) implements CommandOrEvent {
    }

    record AvailableServicesChangedEvent(Set<String> services) implements CommandOrEvent {
    }
}
