import { inject, Injectable } from '@angular/core';
import { CommandOrEventName, ExecuteScriptCommand } from '../../../../model/command-or-event.interface';
import { EXECUTE_SCRIPT_COMMAND } from '../../../../model/commands';
import { DevConsoleStore } from '../../../../store/dev-console.store';
import { WebSocketService } from '../../../web-socket/web-socket.service';
import { Handler } from '../../handler.interface';

@Injectable({ providedIn: 'root'})
export class ExecuteScriptCommandHandler implements Handler<ExecuteScriptCommand> {
  private readonly store = inject(DevConsoleStore);
  private readonly webSocketService = inject(WebSocketService);

  public canHandle(): CommandOrEventName {
    return EXECUTE_SCRIPT_COMMAND;
  }

  public handle(command: ExecuteScriptCommand): void {
    const { uuid } = command;
    const message = this.store.getMessageByUuid(uuid);

    if (!message) return;

    this.store.setCellStatus(uuid, 'running');
    this.webSocketService.send(command);
  }
}
