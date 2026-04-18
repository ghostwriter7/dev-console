import { computed } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { ErrorResult, SuccessResult } from '../model/command-or-event.interface';
import { v4 as uuid } from 'uuid';
import { UiMessage } from '../model/ui-message.interface';

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
  { providedIn: 'root' },
  withState(initialState),
  withMethods((store) => ({
    getMessageByUuid: (uuid: string): UiMessage | undefined => store.messages().find((m) => m.uuid === uuid),
  })),
  withComputed((store) => ({
    hasAnyMessages: computed(() => store.messages().length > 0)
  })),
  withMethods((store) => {
      const getCellIndex = (uuid: string): number => store.messages().findIndex((m) => m.uuid === uuid);


      return {
        activateCell: (cellId: string): void => patchState(store, { activeCell: cellId }),

        addBlankCell: () => {
          const availableServices = store.availableServices();
          const message: UiMessage = {
            service: availableServices[0],
            status: 'idle',
            script: '',
            uuid: uuid(),
          };
          patchState(store, ({ messages: [...store.messages(), message] }));
        },

        deactivateCell: (): void => patchState(store, { activeCell: null }),

        insertMessage: (message: Omit<UiMessage, 'result'>): void =>
          patchState(store, ({ messages: [...store.messages(), message] })),

        removeCell: (uuid: string): void =>
          patchState(store, ({ messages: store.messages().filter((m) => m.uuid !== uuid) })),

        setAvailableServices: (services: string[]): void => patchState(store, { availableServices: services }),

        setActiveServiceForCell: (uuid: string, serviceName: string): void => {
          const cellIndex = getCellIndex(uuid);

          if (cellIndex === -1) return;
          const messages = store.messages();
          patchState(store, ({
            messages: messages.with(cellIndex, {
              ...messages[cellIndex],
              service: serviceName,
            })
          }));
        },

        setCellStatus: (uuid: string, status: UiMessage['status']): void => {
          const cellIndex = getCellIndex(uuid);

          if (cellIndex === -1) return;

          const messages = store.messages();
          patchState(store, ({
            messages: messages.with(cellIndex, {
              ...messages[cellIndex],
              status,
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
          const cellIndex = getCellIndex(uuid);

          if (cellIndex === -1) return;

          const messages = store.messages();
          patchState(store, ({ messages: messages.with(cellIndex, { ...messages[cellIndex], result }) }));
        }
      }
    }
  )
);

