import { inject, Injectable } from '@angular/core';
import { DevConsoleStore } from '../../store/dev-console.store';import { EventService } from '../event-service/event.service';
import { MESSAGE_RECEIVED } from '../../events';
import { CommandOrEvent, isCommandOrEvent } from '../../model/command-or-event.interface';
import { v4 as uuid } from 'uuid';

@Injectable({ providedIn: 'root' })
export class MediatorService {

  private readonly store = inject(DevConsoleStore);
  private readonly eventService = inject(EventService);

  public initialize(): void {
    this.eventService.getStream$(MESSAGE_RECEIVED)
      .subscribe((payload) => this.handle(payload));
  }

  private handle(payload: unknown): void {
    const commandOrEvent = this.tryParse(payload);

    if (!commandOrEvent) return;

    this.eventService.emit(commandOrEvent.type, commandOrEvent);

    switch (commandOrEvent.type) {
      case 'availableServicesChangedEvent': {
        const { services } = commandOrEvent;
        this.store.setAvailableServices(services);

        if (!this.store.hasAnyMessages()) {
          this.store.insertMessage({
            service: services[0],
            status: 'idle',
            script: '',
            uuid: uuid(),
          });
        }
        break;
      }
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
