import { Component, computed, inject, input } from '@angular/core';
import { ClrAccordionModule, ClrIcon, ClrIfExpanded } from "@clr/angular";
import { DevConsoleStore } from '../../store/dev-console.store';
import { ErrorRenderer } from "../error-renderer/error-renderer.component";
import { MessageGroupActions } from "../message-group-actions/message-group-actions";
import { UiMessageGroup } from '../../model/ui-message-group.interface';
import { OutputRenderer } from "../output-renderer/output-renderer";
import { ScriptControl } from "../script-control/script-control.component";

@Component({
  selector: 'app-message-group',
  imports: [
    ClrAccordionModule,
    ErrorRenderer,
    MessageGroupActions,
    OutputRenderer,
    ScriptControl,
    ClrIfExpanded,
    ClrIcon
  ],
  templateUrl: './message-group.html',
  styleUrl: './message-group.css',
})
export class MessageGroup {
  public index = input.required<number>();
  public group = input.required<UiMessageGroup>();

  protected readonly oneBasedIndex = computed(() => this.index() + 1);
  private readonly store = inject(DevConsoleStore);

  protected removeCell(): void {
    this.store.removeCell(this.group().uuid);
  }
}
