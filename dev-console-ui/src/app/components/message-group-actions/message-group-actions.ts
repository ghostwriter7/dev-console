import { Component, computed, DestroyRef, inject, input, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, NonNullableFormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ClrComboboxModule, ClrCommonFormsModule } from '@clr/angular';
import { DevConsoleStore } from '../../store/dev-console.store';
import { UiMessageGroup } from '../../model/ui-message-group.interface';
import { ScriptExecutorService } from '../../services/script-executor/script-executor.service';

@Component({
  selector: 'app-message-group-actions',
  imports: [
    ClrComboboxModule,
    ClrCommonFormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './message-group-actions.html',
  styleUrl: './message-group-actions.css',
})
export class MessageGroupActions implements OnInit {
  public group = input.required<UiMessageGroup>();

  private readonly store = inject(DevConsoleStore);

  protected readonly availableServices = this.store.availableServices;
  protected readonly hasNoServices = computed(() => !this.availableServices().length);

  protected serviceControl!: FormControl<string>;

  private readonly destroyRef = inject(DestroyRef);
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly scriptExecutorService = inject(ScriptExecutorService);
  protected readonly isCellRunning = computed(() => this.group().status === 'running');

  public ngOnInit(): void {
    this.serviceControl = this.formBuilder.control(this.group().service);

    this.serviceControl.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((service) => this.store.setActiveServiceForCell(this.group().uuid, service));
  }

  protected runScript(): void {
    this.scriptExecutorService.executeScript(this.group().uuid);
  }
}
