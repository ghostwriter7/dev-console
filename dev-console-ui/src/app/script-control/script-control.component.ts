import { Component, computed, effect, ElementRef, input, model, OnInit, viewChild } from '@angular/core';
import { outputFromObservable } from '@angular/core/rxjs-interop';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ClrComboboxModule } from '@clr/angular';
import * as monaco from 'monaco-editor'

@Component({
  selector: 'app-script-control',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    ClrComboboxModule,
  ],
  templateUrl: './script-control.component.html',
  styleUrl: './script-control.css',
})
export class ScriptControl implements OnInit {
  public availableServices = input.required<string[]>();
  public payload = model.required<string>();

  protected readonly hasNoServices = computed(() => !this.availableServices().length);

  private readonly editorContainer = viewChild.required<ElementRef<HTMLDivElement>>('editor');

  private editor!: monaco.editor.IStandaloneCodeEditor;
  protected readonly serviceControl = new FormControl<string | null>(null);

  public activeService = outputFromObservable(this.serviceControl.valueChanges);

  constructor() {
    effect(() => {
      const control = this.serviceControl;
      const services = this.availableServices();
      if (!services.length) {
        control.reset({ value: null, disabled: true });
      } else if (!control.value) {
        control.setValue(services[0]);
        if (control.disabled) {
          control.enable();
        }
      }
    });
  }


  public ngOnInit(): void {
    this.editor = monaco.editor.create(this.editorContainer().nativeElement, {
      language: 'java',
      theme: 'vs-dark',
      automaticLayout: true,
      lineNumbers: 'on',
      value: this.payload(),
    });
  }

  protected run(): void {
    const value = this.editor.getValue();
    this.payload.set(value || '');
  }
}
