package com.rozplochowski.devconsole.handler.handlers;

import com.rozplochowski.devconsole.CommandOrEvent;
import com.rozplochowski.devconsole.Handles;
import com.rozplochowski.devconsole.handler.AvailableServicesChangedNotifier;
import com.rozplochowski.devconsole.common.MessageSender;
import com.rozplochowski.devconsole.handler.ServerContextRepository;
import com.rozplochowski.devconsole.handler.SessionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Handles(CommandOrEvent.ClientConnectCommand.class)
class ClientConnectCommandHandler implements com.rozplochowski.devconsole.MessageHandler<CommandOrEvent.ClientConnectCommand> {
    private final ServerContextRepository serverContextRepository;
    private final SessionRepository sessionRepository;
    private final MessageSender sender;
    private final AvailableServicesChangedNotifier availableServicesChangedNotifier;

    @Override
    public void handle(CommandOrEvent.ClientConnectCommand message, WebSocketSession session) {
        log.info("Received ClientConnectCommand from {}. UI connected.", session.getId());
        sessionRepository.put("CLIENT", session);
        availableServicesChangedNotifier.notifyClient();
        session.getAttributes().put("serviceName", "CLIENT");
        var serverContexts = serverContextRepository.entries();

        if (!serverContexts.isEmpty()) {
            serverContexts.forEach(entry ->
                    sender.send(session, new CommandOrEvent.InstallTypeMetadataCommand(entry.getKey(), entry.getValue().typeMetadata(), entry.getValue().symbols())));
        }
    }
}
