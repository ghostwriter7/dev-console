import { Component, ElementRef, inject, input, OnDestroy, OnInit, viewChild } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ClrComboboxModule } from '@clr/angular';
import * as monaco from 'monaco-editor';
import { UiMessageGroup } from '../../model/ui-message-group.interface';
import { ScriptEngineFacade } from '../../script-engine/facade/script-engine.facade';
import { DevConsoleStore } from '../../store/dev-console.store';

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
  public group = input.required<UiMessageGroup>();

  private readonly editorContainer = viewChild.required<ElementRef<HTMLDivElement>>('editor');

  private editor!: monaco.editor.IStandaloneCodeEditor;
  private readonly disposables: monaco.IDisposable[] = [];
  private readonly store = inject(DevConsoleStore);
  private readonly scriptEngineFacade = inject(ScriptEngineFacade);

  public ngOnInit(): void {
    this.editor = monaco.editor.create(this.editorContainer().nativeElement, {
      language: 'java',
      theme: 'vs-dark',
      wordBasedSuggestions: 'off',
      automaticLayout: true,
      lineNumbers: 'on',
      value: this.group().script,
    });

    this.disposables.push(
      this.editor.onDidBlurEditorWidget(() => {
        if (this.store.activeCell() === this.group().uuid) this.store.deactivateCell();
      }),
      this.editor.onDidFocusEditorWidget(() => this.store.activateCell(this.group().uuid))
    );

    this.editor.onDidChangeModelContent(() => {
      const value = this.editor.getValue();
      this.store.updateScript(value);

      this.scriptEngineFacade.updateStatements(this.group().uuid, this.group().service, value);
    });

    monaco.languages.registerCompletionItemProvider('java', {
      triggerCharacters: ['.', '('],
      provideCompletionItems: (model, position, context, token) => {
        const word = model.getWordUntilPosition(position);

        const range = {
          startLineNumber: position.lineNumber,
          endLineNumber: position.lineNumber,
          startColumn: word.startColumn,
          endColumn: word.endColumn
        };

        return {
          suggestions: this.scriptEngineFacade.getSuggestions(this.editor.getValue(), this.group().uuid, this.group().service, position.lineNumber, position.column)
            .map((suggestion) => ({
              ...suggestion,
              range
            }))
        };
      }
    })
  }

  public ngOnDestroy(): void {
    this.disposables.forEach((d) => d.dispose());
  }
}
