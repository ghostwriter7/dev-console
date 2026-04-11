import { computed } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { ErrorResult, SuccessResult } from './model/command-or-event.interface';
import { v4 as uuid } from 'uuid';

interface UiMessage {
  service: string;
  script: string;
  result?: ErrorResult | SuccessResult;
  uuid: string;
}

type DevConsoleStore = {
  activeCell: string | null;
  availableServices: string[];
  messages: UiMessage[];
}

const initialState: DevConsoleStore = {
  activeCell: null,
  availableServices: [],
  messages: [],
};

export const DevConsoleStore = signalStore(
  { providedIn: 'root'},
    withState(initialState),
    withComputed((store) => ({
      hasAnyMessages: computed(() => store.messages().length > 0)
    })),
    withMethods((store) => ({
      activateCell: (cellId: string): void => patchState(store, { activeCell: cellId }),

      addBlankCell: () => {
        const availableServices = store.availableServices();
        const message: UiMessage = {
          service: availableServices[0],
          script: '',
          uuid: uuid(),
        };
        patchState(store, ({ messages: [...store.messages(), message] }));
      },

      deactivateCell: (): void => patchState(store, { activeCell: null }),

      getMessageByUuid: (uuid: string): UiMessage | undefined => store.messages().find((m) => m.uuid === uuid),

      insertMessage: (message: Pick<UiMessage, 'service' | 'script' | 'uuid'>): void =>
        patchState(store, ({ messages: [...store.messages(), message] })),

      setAvailableServices: (services: string[]): void => patchState(store, { availableServices: services }),

      setActiveServiceForCell: (cellId: string, serviceName: string): void => {
        const cellIndex = store.messages().findIndex((m) => m.uuid === cellId);

        if (cellIndex === -1) return;
        const messages = store.messages();
        patchState(store, ({
          messages: messages.with(cellIndex, {
            ...messages[cellIndex],
            service: serviceName,
          })
        }));
      },

      updateScript: (script: string): void => {
        const activeCellUuid = store.activeCell();

        if (!activeCellUuid) return;

        const messages = store.messages();
        const activeCellIndex = messages.findIndex((m) => m.uuid === activeCellUuid);
        const activeCell = messages[activeCellIndex];

        patchState(store, ({ messages: messages.with(activeCellIndex, { ...activeCell, script }) }));
      },

      updateResult: (uuid: string, result: ErrorResult | SuccessResult): void => {
        const cellIndex = store.messages().findIndex((m) => m.uuid === uuid);

        if (cellIndex === -1) return;

        const messages = store.messages();
        patchState(store, ({ messages: messages.with(cellIndex, { ...messages[cellIndex], result }) }));
      }
    }))
  )
;

