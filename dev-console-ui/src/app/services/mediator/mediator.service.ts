import { inject, Injectable } from '@angular/core';
import { EventService } from '../event-service/event.service';
import { CommandOrEvent, CommandOrEventName, isCommandOrEvent } from '../../model/command-or-event.interface';
import { Handler } from '../handlers/handler.interface';
import { COMMAND_OR_EVENT_HANDLER } from '../handlers/handler.token';

@Injectable({ providedIn: 'root' })
export class MediatorService {

  private readonly eventService = inject(EventService);
  private readonly handlers: Map<CommandOrEventName, Handler<CommandOrEvent>> =
    new Map(inject(COMMAND_OR_EVENT_HANDLER).map((handler) => [handler.canHandle(), handler]))


  public initialize(): void {
    this.eventService.events$
      .subscribe(({ type, payload }) => {
        if (this.handlers.has(type) && isCommandOrEvent(payload)) {
          this.handlers.get(type)?.handle(payload);
        } else {
          this.handle(payload);
        }
      })
  }

  private handle(payload: unknown): void {
    const commandOrEvent = this.tryParse(payload);

    if (!commandOrEvent) return;

    const handler = this.handlers.get(commandOrEvent.type);

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
