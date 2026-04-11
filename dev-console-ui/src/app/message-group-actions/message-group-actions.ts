import { Component, computed, effect, input } from '@angular/core';
import { outputFromObservable } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ClrComboboxModule, ClrCommonFormsModule } from '@clr/angular';
import * as monaco from 'monaco-editor';

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
export class MessageGroupActions {
  public availableServices = input.required<string[]>();

  protected readonly hasNoServices = computed(() => !this.availableServices().length);
  protected readonly serviceControl = new FormControl<string | null>(null);

  public activeService = outputFromObservable(this.serviceControl.valueChanges);

  constructor() {
    effect(() => {
      const control = this.serviceControl;
      const services = this.availableServices();
      if (!services.length) {
        control.reset({value: null, disabled: true});
      } else if (!control.value) {
        control.setValue(services[0]);
        if (control.disabled) {
          control.enable();
        }
      }
    });
  }
}
