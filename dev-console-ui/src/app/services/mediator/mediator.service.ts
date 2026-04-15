import { inject, Injectable } from '@angular/core';
import { EventService } from '../event-service/event.service';
import { MESSAGE_RECEIVED } from '../../events';
import { CommandOrEvent, isCommandOrEvent } from '../../model/command-or-event.interface';
import { Handler } from '../handlers/handler.interface';
import { COMMAND_OR_EVENT_HANDLER } from '../handlers/handler.token';

@Injectable({ providedIn: 'root' })
export class MediatorService {

  private readonly eventService = inject(EventService);
  private readonly handlers: Handler<CommandOrEvent>[] = inject(COMMAND_OR_EVENT_HANDLER);

  public initialize(): void {
    this.eventService.getStream$(MESSAGE_RECEIVED)
      .subscribe((payload) => this.handle(payload));
  }

  private handle(payload: unknown): void {
    const commandOrEvent = this.tryParse(payload);

    if (!commandOrEvent) return;

    const handler = this.handlers.find((handler) => handler.canHandle(commandOrEvent));

    if (handler) {
      handler.handle(commandOrEvent);
    }
  }

  private tryParse(payload: unknown): CommandOrEvent | null {
    if (typeof payload !== 'string') return null;

    try {
      const mayBeCommandOrEvent = JSON.parse(payload);
      return isCommandOrEvent(mayBeCommandOrEvent) ? mayBeCommandOrEvent : null;
    } catch {
      return null;
    }
  }
}
