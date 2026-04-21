package com.rozplochowski.devconsole.handler.handlers;


import com.rozplochowski.devconsole.*;
import com.rozplochowski.devconsole.handler.*;
import com.rozplochowski.devconsole.common.MessageSender;import com.rozplochowski.devconsole.handler.repository.ServerContextRepository;import com.rozplochowski.devconsole.handler.repository.SessionRepository;import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Handles(CommandOrEvent.ServerConnectCommand.class)
class ServerConnectCommandHandler implements MessageHandler<CommandOrEvent.ServerConnectCommand> {
    private final SessionRepository sessionRepository;
    private final ServerContextRepository serverContextRepository;
    private final MessageSender sender;
    private final AvailableServicesChangedNotifier availableServicesChangedNotifier;

    @Override
    public void handle(CommandOrEvent.ServerConnectCommand message, WebSocketSession session) {
        var serviceName = message.serviceName();

        handleServiceRegistration(serviceName, session);
        handleTypeMetadataAndSymbols(serviceName, message.typeMetadata(), message.symbols());
    }

    private void handleServiceRegistration(String serviceName, WebSocketSession session) {
        log.info("Received ServerConnectCommand from {}", serviceName);
        sessionRepository.put(serviceName, session);
        session.getAttributes().put(SessionAttribute.SERVICE_NAME.name(), serviceName);

        var payload = new CommandOrEvent.ServerConnectedEvent(serviceName);
        sender.send(session, payload);
    }

    private void handleTypeMetadataAndSymbols(String serviceName, List<TypeMetadata> typeMetadata, List<Symbol> symbols) {
        serverContextRepository.put(serviceName, new ServerContext(typeMetadata, symbols));
        sessionRepository.tryGetClient().ifPresent(client -> {
            availableServicesChangedNotifier.notifyClient(client);
            var payload = new CommandOrEvent.InstallTypeMetadataCommand(serviceName, typeMetadata, symbols);
            sender.send(client, payload);
        });
    }

}
