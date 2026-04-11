import { Component, computed, inject } from '@angular/core';
import { DevConsoleStore } from '../dev-console.store';
import { MessageGroup } from '../message-group/message-group';
import { UiMessageGroup } from '../model/ui-message-group.interface';

@Component({
  selector: 'app-message-group-list',
  imports: [
    MessageGroup
  ],
  templateUrl: './message-group-list.html',
  styleUrl: './message-group-list.css',
})
export class MessageGroupList {
  private readonly store = inject(DevConsoleStore);

  protected readonly groupedMessages = computed<UiMessageGroup[]>(() =>
    this.store.messages().map((message) => ({
      ...message,
      hasNoOutput: !message.result
    })));
}
