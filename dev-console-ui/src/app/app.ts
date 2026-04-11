import { Component, computed, OnInit, signal, viewChildren } from '@angular/core';
import { ErrorRenderer } from './error-renderer/error-renderer.component';
import { MessageGroupActions } from './message-group-actions/message-group-actions';
import { OutputRenderer } from './output-renderer/output-renderer';
import { ScriptControl } from './script-control/script-control.component';
import { v4 as uuid } from 'uuid';
import {
  AvailableServicesChangedEvent,
  CommandOrEvent,
  ErrorResult,
  ScriptExecutedEvent,
  SuccessResult
} from './command-or-event.interface';
import {
  ErrorMessage,
  InputMessage,
  isErrorMessage,
  isInputMessage,
  SuccessMessage,
  UiMessage
} from './ui-message.interface';
import {
  ClrAccordionModule,
  ClrComboboxModule,
  ClrCommonFormsModule,
  ClrMainContainerModule,
  ClrNavigationModule
} from '@clr/angular';
import { Nav } from './nav/nav';

const isSuccessResponse = (event: ScriptExecutedEvent): event is ScriptExecutedEvent<SuccessResult> =>
  event.scriptResult.hasOwnProperty('result') && event.scriptResult.hasOwnProperty('stdOut');
const isErrorResponse = (event: ScriptExecutedEvent): event is ScriptExecutedEvent<ErrorResult> =>
  event.scriptResult.hasOwnProperty('error') && event.scriptResult.hasOwnProperty('stackTrace');

interface MessageGroup {
  input: InputMessage;
  error?: ErrorMessage;
  output?: SuccessMessage;
  uuid: string;
  hasNoOutput: boolean;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  imports: [
    ScriptControl,
    ErrorRenderer,
    ClrMainContainerModule,
    Nav,
    ClrNavigationModule,
    OutputRenderer,
    ClrComboboxModule,
    ClrCommonFormsModule,
    MessageGroupActions,
    ClrAccordionModule
  ],
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly availableServices = signal<string[]>([]);
  private readonly messages = signal<UiMessage[]>([{
    type: 'input',
    payload: '',
    uuid: uuid(),
  }]);
  private readonly scriptControls = viewChildren(ScriptControl);

  protected readonly groupedMessages = computed<MessageGroup[]>(() =>
    this.messages().reduce((groups, message) => {
      if (isInputMessage(message)) {
        groups.push({
          input: message,
          uuid: message.uuid,
          hasNoOutput: true,
        });
      } else {
        const lastGroup = groups.at(-1)!;
        lastGroup.hasNoOutput = false;
        if (lastGroup) {
          if (isErrorMessage(message)) {
            lastGroup.error = message;
          } else {
            lastGroup.output = message;
          }
        }
      }

      return groups;
    }, [] as MessageGroup[])
  )

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
      const idx = messages.findIndex((m) => m.uuid === event.uuid && m.type !== 'input');

      if (idx !== -1) return messages.with(idx, uiMessage);
      return [...messages, uiMessage, {
        type: 'input',
        payload: '',
        uuid: uuid(),
      }];
    });
  }

  protected onActiveServiceChange(serviceName: string | null): void {
    this.activeService = serviceName;
  }

  protected runScript(uuid: string): void {
    const scriptControls = this.scriptControls();
    const targetControl = scriptControls.find((scriptControl) => scriptControl.uuid() === uuid);

    const script = targetControl?.getScript();

    if (this.ws && script && this.activeService) {
      this.ws.send(JSON.stringify({
        targetServiceName: this.activeService,
        uuid,
        type: 'executeScriptCommand',
        script
      }));
    }
  }
}
