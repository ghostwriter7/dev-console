import { Component, inject } from '@angular/core';
import { DevConsoleStore } from '../dev-console.store';

@Component({
  selector: 'app-add-cell-button',
  templateUrl: './add-cell-button.html',
  styleUrl: './add-cell-button.css',
})
export class AddCellButton {
  private readonly store = inject(DevConsoleStore);

  protected addCell(): void {
    this.store.addBlankCell();
  }
}
