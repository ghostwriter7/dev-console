import { inject, Injectable } from '@angular/core';
import { DevConsoleStore } from '../../../store/dev-console.store';
import { SCRIPT_EXECUTED_EVENT } from '../../../events';
import {
  CommandOrEvent,
  ExecuteScriptCommand,
  ScriptExecutedEvent,
} from '../../../model/command-or-event.interface';
import { Handler } from '../handler.interface';
import { WebSocketService } from '../../web-socket/web-socket.service';

@Injectable({ providedIn: 'root' })
export class ScriptExecutorService implements Handler<ScriptExecutedEvent>{

  private readonly store = inject(DevConsoleStore);
  private readonly webSocketService = inject(WebSocketService);

  public executeScript(uuid: string): void {
    const message = this.store.getMessageByUuid(uuid);

    if (!message) return;

    this.store.setCellStatus(uuid, 'running');
    const { service, script } = message;
    const command: ExecuteScriptCommand = {
      type: 'executeScriptCommand',
      uuid,
      targetServiceName: service,
      script
    };

    this.webSocketService.send(command);
  }

  public canHandle(commandOrEvent: CommandOrEvent): boolean {
    return commandOrEvent.type === SCRIPT_EXECUTED_EVENT;
  }

  public handle(event: ScriptExecutedEvent): void {
    const scriptResult = event.scriptResult;
    this.store.setCellStatus(event.uuid, 'idle');
    this.store.updateResult(event.uuid, scriptResult);
  }
}
