import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';

type DevConsoleStore = {
  activeCell: string | null;
}

const initialState: DevConsoleStore = {
  activeCell: null,
};

export const DevConsoleStore = signalStore(
  withState(initialState),
  withMethods((store) => ({
    activateCell: (cellId: string): void => patchState(store, { activeCell: cellId }),
    deactivateCell: (): void => patchState(store, { activeCell: null }),
  }))
);

