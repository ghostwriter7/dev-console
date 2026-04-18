import { inject, Injectable } from '@angular/core';
import { MESSAGE_RECEIVED_EVENT } from '../../model/events';
import { CommandOrEvent } from '../../model/command-or-event.interface';
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

  public send(command: CommandOrEvent): void {
    this.ws.send(JSON.stringify(command));
  }

  public sendSync<R>(command: CommandOrEvent, timeout = 5000): Promise<R> {
    this.ws.send(JSON.stringify(command));

    const chainId = command.id;
    return new Promise((resolve, reject) => {
      const abortController = new AbortController();
      const signal = abortController.signal;

      let received = false;

      const handler = (event: MessageEvent<string>) => {
        const { data } = event;
        const message = JSON.parse(data) as CommandOrEvent;
        if (message.id === chainId) {
          received = true;
          this.ws.removeEventListener('message', handler);
          resolve(message as R);
        }
      };

      setInterval(() => {
        if (!received) {
          abortController.abort(`Operation ID: ${chainId} timed out after ${timeout}ms`);
          this.ws.removeEventListener('message', handler);
          reject(new Error(`Operation ID: ${chainId} timed out after ${timeout}ms`));
        }
      }, timeout);

      this.ws.addEventListener('message', handler, { signal });
    });
  }
}
