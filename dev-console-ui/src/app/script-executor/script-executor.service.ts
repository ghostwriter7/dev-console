import { inject, Injectable } from '@angular/core';
import { filter } from 'rxjs';
import { DevConsoleStore } from '../dev-console.store';
import { EventService } from '../event-service/event.service';
import { SCRIPT_EXECUTED_EVENT } from '../events';
import {
  ExecuteScriptCommand,
  ScriptExecutedEvent,
} from '../model/command-or-event.interface';
import { WebSocketService } from '../web-socket/web-socket.service';

function isScriptExecutedEvent(payload: unknown): payload is ScriptExecutedEvent {
  return payload !== null && typeof payload === 'object' && 'type' in payload && payload.type === SCRIPT_EXECUTED_EVENT;
}

@Injectable({ providedIn: 'root' })
export class ScriptExecutorService {

  private readonly store = inject(DevConsoleStore);
  private readonly webSocketService = inject(WebSocketService);
  private readonly eventService = inject(EventService);

  constructor() {
    this.eventService.getStream$(SCRIPT_EXECUTED_EVENT)
      .pipe(filter(isScriptExecutedEvent))
      .subscribe((event) => this.handleScriptExecutedEvent(event));
  }

  public executeScript(uuid: string): void {
    const message = this.store.getMessageByUuid(uuid);

    if (!message) return;

    const { service, script } = message;
    const command: ExecuteScriptCommand = {
      type: 'executeScriptCommand',
      uuid,
      targetServiceName: service,
      script
    };

    this.webSocketService.send(command);
  }

  public handleScriptExecutedEvent(event: ScriptExecutedEvent): void {
    const scriptResult = event.scriptResult;

    if (!scriptResult) return;

    this.store.updateResult(event.uuid, scriptResult);
  }
}
