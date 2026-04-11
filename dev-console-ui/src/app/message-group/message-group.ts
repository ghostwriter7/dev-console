import { Component, computed, input } from '@angular/core';
import { ClrAccordionModule, ClrIfExpanded } from "@clr/angular";
import {ErrorRenderer} from "../error-renderer/error-renderer.component";
import {MessageGroupActions} from "../message-group-actions/message-group-actions";
import { ErrorResult, SuccessResult } from '../model/command-or-event.interface';
import { UiMessageGroup } from '../model/ui-message-group.interface';
import {OutputRenderer} from "../output-renderer/output-renderer";
import {ScriptControl} from "../script-control/script-control.component";

@Component({
  selector: 'app-message-group',
  imports: [
    ClrAccordionModule,
    ErrorRenderer,
    MessageGroupActions,
    OutputRenderer,
    ScriptControl,
    ClrIfExpanded
  ],
  templateUrl: './message-group.html',
  styleUrl: './message-group.css',
})
export class MessageGroup {
  public index = input.required<number>();
  public group = input.required<UiMessageGroup>();

  protected readonly oneBasedIndex = computed(() => this.index() + 1);
}
