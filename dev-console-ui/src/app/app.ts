import { Component, OnInit, signal } from '@angular/core';
import { OutputRenderer } from './output-renderer/output-renderer';
import { ScriptControl } from './script-control/script-control.component';
import { ErrorMessage } from './error-message/error-message.component';
import { v4 as uuid } from 'uuid';
import {
  AvailableServicesChangedEvent,
  CommandOrEvent,
  ErrorResult,
  ScriptExecutedEvent,
  SuccessResult
} from './command-or-event.interface';
import { UiMessage } from './ui-message.interface';
import { ClrMainContainerModule, ClrNavigationModule } from '@clr/angular';
import { Nav } from './nav/nav';

const isSuccessResponse = (event: ScriptExecutedEvent): event is ScriptExecutedEvent<SuccessResult> =>
  event.scriptResult.hasOwnProperty('result') && event.scriptResult.hasOwnProperty('stdOut');
const isErrorResponse = (event: ScriptExecutedEvent): event is ScriptExecutedEvent<ErrorResult> =>
  event.scriptResult.hasOwnProperty('error') && event.scriptResult.hasOwnProperty('stackTrace');

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  imports: [
    ScriptControl,
    ErrorMessage,
    ClrMainContainerModule,
    Nav,
    ClrNavigationModule,
    OutputRenderer
  ],
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly availableServices = signal<string[]>([]);
  protected readonly messages = signal<UiMessage[]>([{
    type: 'input',
    payload: '',
    uuid: uuid(),
  }]);

  private ws?: WebSocket;
  private activeService: string | null = null;

  public ngOnInit(): void {
    this.ws = new WebSocket('ws://localhost:8080/v1/dev/console/ws');

    this.ws.addEventListener('message', (event: MessageEvent<string>) => {
      const {data} = event;
      const commandOrEvent = this.tryParse(data);

      if (!commandOrEvent) return;

      switch (commandOrEvent.type) {
        case 'scriptExecutedEvent':
          this.handleScriptExecutedEvent(commandOrEvent as ScriptExecutedEvent);
          break;
        case 'availableServicesChangedEvent':
          this.availableServices.set((commandOrEvent as AvailableServicesChangedEvent).services);
          if (!this.activeService || !this.availableServices().includes(this.activeService)) {
            this.activeService = this.availableServices()[0];
          }
          break;
      }


    });

    this.ws.addEventListener('open', (event) => {
      console.log('Connected');

      this.ws!.send(JSON.stringify({
          type: 'clientConnectCommand'
        }
      ));
    });

    this.ws.addEventListener('close', (event) => {
      console.log('Disconnected');
    });
  }

  private tryParse(payload: string): CommandOrEvent | null {
    try {
      return JSON.parse(payload);
    } catch {
      return null;
    }
  }

  private createUiMessageFromEvent(event: ScriptExecutedEvent): UiMessage | null {
    if (isSuccessResponse(event)) {
      return {
        type: 'output',
        payload: event.scriptResult,
        uuid: event.uuid,
      };
    } else if (isErrorResponse(event)) {
      return {
        type: 'error',
        payload: event.scriptResult,
        uuid: event.uuid,
      };
    }
    return null;
  }

  private handleScriptExecutedEvent(event: ScriptExecutedEvent): void {
    const uiMessage = this.createUiMessageFromEvent(event);

    if (!uiMessage) return;

    this.messages.update((messages) => {
      const idx = messages.findIndex((m) => m.uuid === event.uuid);

      if (idx !== -1) return messages.with(idx, uiMessage);
      return [...messages, uiMessage];
    });
    this.messages.update((messages) => [...messages, {
      type: 'input',
      payload: '',
      uuid: uuid(),
    }])
  }

  protected onPayloadChange(payload?: string): void {
    if (this.ws && payload && this.activeService) {
      this.ws.send(JSON.stringify({
        targetServiceName: this.activeService,
        uuid: uuid(),
        type: 'executeScriptCommand',
        script: payload
      }));
    }
  }

  protected onActiveServiceChange(serviceName: string | null): void {
    this.activeService = serviceName;
  }
}
