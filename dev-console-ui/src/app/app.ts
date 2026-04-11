import { Component, computed, inject, OnInit } from '@angular/core';
import { AddCellButton } from './add-cell-button/add-cell-button';
import { DevConsoleStore } from './dev-console.store';
import { MediatorService } from './mediator/mediator.service';
import { MessageGroupList } from './message-group-list/message-group-list';
import {
  ClrAccordionModule, ClrAlertModule,
  ClrComboboxModule,
  ClrCommonFormsModule,
  ClrMainContainerModule,
  ClrNavigationModule
} from '@clr/angular';
import { Nav } from './nav/nav';
import { WebSocketService } from './web-socket/web-socket.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  imports: [
    ClrMainContainerModule,
    Nav,
    ClrNavigationModule,
    ClrComboboxModule,
    ClrCommonFormsModule,
    ClrAccordionModule,
    ClrAlertModule,
    MessageGroupList,
    AddCellButton
  ],
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly store = inject(DevConsoleStore);

  protected readonly hasNoAvailableServices = computed(() => !this.store.availableServices().length);

  private readonly webSocketService = inject(WebSocketService);
  private readonly mediatorService = inject(MediatorService);

  public ngOnInit(): void {
    this.mediatorService.initialize();
    this.webSocketService.connect();
  }
}
