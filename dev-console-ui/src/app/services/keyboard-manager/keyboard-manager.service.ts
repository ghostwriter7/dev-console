import { DOCUMENT, inject, Injectable } from '@angular/core';
import { EXECUTE_SCRIPT_COMMAND } from '../../model/commands';
import { DevConsoleStore } from '../../store/dev-console.store';
import { EventService } from '../event-service/event.service';

@Injectable({ providedIn: 'root' })
export class KeyboardManagerService {
  private readonly document = inject(DOCUMENT);
  private readonly store = inject(DevConsoleStore);
  private readonly eventService = inject(EventService);

  public initialize(): void {
    this.document.addEventListener('keydown', (event) => {
      if (event.altKey && event.key === 'Enter') {
        const activeCell = this.store.activeCell();

        if (activeCell) {
          const { uuid, service, script } = this.store.getMessageByUuid(activeCell)!;
          this.eventService.emit(EXECUTE_SCRIPT_COMMAND, {
            type: EXECUTE_SCRIPT_COMMAND,
            uuid,
            targetServiceName: service,
            script
          })
        }
      }
    });

  }
}
