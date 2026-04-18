package com.rozplochowski.devconsole.handler.handlers;


import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.Handles;
import com.rozplochowski.devconsole.Symbol;
import com.rozplochowski.devconsole.TypeMetadata;
import com.rozplochowski.devconsole.handler.*;
import com.rozplochowski.devconsole.common.MessageSender;import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Handles(CommandOrEvent.ServerConnectCommand.class)
class ServerConnectCommandHandler implements com.rozplochowski.devconsole.MessageHandler<CommandOrEvent.ServerConnectCommand> {
    private final SessionRepository sessionRepository;
    private final ServerContextRepository serverContextRepository;
    private final MessageSender sender;
    private final AvailableServicesChangedNotifier availableServicesChangedNotifier;

    @Override
    public void handle(CommandOrEvent.ServerConnectCommand message, WebSocketSession session) {
        var serviceName = message.serviceName();
        var typeMetadata = message.typeMetadata();
        var symbols = message.symbols();

        handleServiceRegistration(serviceName, session);
        handleTypeMetadataAndSymbols(serviceName, typeMetadata, symbols);
    }

    private void handleServiceRegistration(String serviceName, WebSocketSession session) {
        log.info("Received ServerConnectCommand from {}", serviceName);
        sessionRepository.put(serviceName, session);
        session.getAttributes().put("serviceName", serviceName);

        var payload = new CommandOrEvent.ServerConnectedEvent(serviceName);
        sender.send(session, payload);

        availableServicesChangedNotifier.notifyClient();
    }

    private void handleTypeMetadataAndSymbols(String serviceName, List<TypeMetadata> typeMetadata, List<Symbol> symbols) {
        serverContextRepository.put(serviceName, new ServerContext(typeMetadata, symbols));
        sessionRepository.getClient().ifPresent(client -> {
            var payload = new CommandOrEvent.InstallTypeMetadataCommand(serviceName, typeMetadata, symbols);
            sender.send(client, payload);
        });
    }

}
