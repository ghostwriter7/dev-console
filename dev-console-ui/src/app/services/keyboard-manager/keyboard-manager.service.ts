import { DOCUMENT, inject, Injectable } from '@angular/core';
import { DevConsoleStore } from '../../store/dev-console.store';
import { ScriptExecutorService } from '../script-executor/script-executor.service';

@Injectable({ providedIn: 'root' })
export class KeyboardManagerService {
  private readonly document = inject(DOCUMENT);
  private readonly store = inject(DevConsoleStore);
  private readonly scriptExecutor = inject(ScriptExecutorService);

  public initialize(): void {
    this.document.addEventListener('keydown', (event) => {
      if (event.altKey && event.key === 'Enter') {
        const activeCell = this.store.activeCell();

        if (activeCell) {
          this.scriptExecutor.executeScript(activeCell);
        }
      }
    });

  }
}
