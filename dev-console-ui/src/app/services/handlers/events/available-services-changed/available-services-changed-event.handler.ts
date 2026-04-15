import { inject, Injectable } from '@angular/core';
import { v4 as uuid } from 'uuid';
import { AVAILABLE_SERVICES_CHANGED_EVENT } from '../../../../model/events';
import { AvailableServicesChangedEvent, CommandOrEventName } from '../../../../model/command-or-event.interface';
import { DevConsoleStore } from '../../../../store/dev-console.store';
import { Handler } from '../../handler.interface';

@Injectable({ providedIn: 'root' })
export class AvailableServicesChangedEventHandler implements Handler<AvailableServicesChangedEvent> {

  private readonly store = inject(DevConsoleStore);

  public handle({ services }: AvailableServicesChangedEvent): void {
    this.store.setAvailableServices(services);

    if (!this.store.hasAnyMessages()) {
      this.store.insertMessage({
        service: services[0],
        status: 'idle',
        script: '',
        uuid: uuid(),
      });
    }
  }

  public canHandle(): CommandOrEventName {
    return AVAILABLE_SERVICES_CHANGED_EVENT;
  }
}
