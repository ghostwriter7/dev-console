import { Component, ElementRef, inject, input, OnDestroy, OnInit, output, viewChild } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ClrComboboxModule } from '@clr/angular';
import * as monaco from 'monaco-editor'
import { DevConsoleStore } from '../dev-console.store';

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
export class ScriptControl implements OnInit, OnDestroy {
  public uuid = input.required<string>();
  public payload = input.required<string>();

  private readonly editorContainer = viewChild.required<ElementRef<HTMLDivElement>>('editor');

  private editor!: monaco.editor.IStandaloneCodeEditor;
  private readonly disposables: monaco.IDisposable[] = [];
  private readonly store = inject(DevConsoleStore);

  public ngOnInit(): void {
    this.editor = monaco.editor.create(this.editorContainer().nativeElement, {
      language: 'java',
      theme: 'vs-dark',
      automaticLayout: true,
      lineNumbers: 'on',
      value: this.payload(),
    });

    this.disposables.push(
      this.editor.onDidBlurEditorWidget(() => this.store.deactivateCell()),
      this.editor.onDidFocusEditorWidget(() => this.store.activateCell(this.uuid()))
    );
  }

  public ngOnDestroy(): void {
    this.disposables.forEach((d) => d.dispose());
  }

  public getScript(): string {
    const value = this.editor.getValue();
    return value.trim();
  }
}
