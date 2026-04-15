import { inject, Injectable } from '@angular/core';
import { DevConsoleStore } from '../../../../store/dev-console.store';
import { SCRIPT_EXECUTED_EVENT } from '../../../../model/events';
import {
  CommandOrEventName,
  ScriptExecutedEvent,
} from '../../../../model/command-or-event.interface';
import { Handler } from '../../handler.interface';

@Injectable({ providedIn: 'root' })
export class ScriptExecutedEventHandler implements Handler<ScriptExecutedEvent>{

  private readonly store = inject(DevConsoleStore);

  public canHandle(): CommandOrEventName {
    return SCRIPT_EXECUTED_EVENT;
  }

  public handle(event: ScriptExecutedEvent): void {
    const scriptResult = event.scriptResult;
    this.store.setCellStatus(event.uuid, 'idle');
    this.store.updateResult(event.uuid, scriptResult);
  }
}
