import { inject, Injectable } from '@angular/core';
import { MESSAGE_RECEIVED_EVENT } from '../../model/events';
import { BaseCommandOrEvent } from '../../model/command-or-event.interface';
import { EventService } from '../event-service/event.service';

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private ws!: WebSocket;
  private readonly eventService = inject(EventService);

  public connect(): void {
    this.ws = new WebSocket('ws://localhost:11111/v1/dev/console/ws');

    this.ws.addEventListener('message', (event: MessageEvent<string>) => {
      this.eventService.emit(MESSAGE_RECEIVED_EVENT, event.data);
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

  public send(command: BaseCommandOrEvent): void {
    this.ws.send(JSON.stringify(command));
  }
}
