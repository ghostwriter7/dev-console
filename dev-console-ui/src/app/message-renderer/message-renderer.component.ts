import {AfterViewInit, Component, ElementRef, input, model, OnInit, viewChild} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api.js';
import 'monaco-editor/esm/vs/language/json/monaco.contribution';
import 'monaco-editor/esm/vs/basic-languages/java/java.contribution.js';

@Component({
  selector: 'app-message-renderer',
  imports: [
    FormsModule,
    ReactiveFormsModule,
  ],
  templateUrl: './message-renderer.component.html',
  styleUrl: './message-renderer.component.css',
})
export class MessageRenderer implements AfterViewInit, OnInit {
  public readOnly = input(false);
  public language = input('java');
  public payload = model.required<string>();

  private readonly editorContainer = viewChild.required<ElementRef<HTMLDivElement>>('editor');

  private editor!: monaco.editor.IStandaloneCodeEditor;

  public ngOnInit(): void {
    this.editor = monaco.editor.create(this.editorContainer().nativeElement, {
      value: this.payload(),
      language: this.language(),
      theme: 'vs-dark',
      automaticLayout: true,
      lineNumbers: 'on',
      readOnly: this.readOnly()
    });
  }

  public ngAfterViewInit(): void {
    if (this.language() === 'json') {
      this.editor.getAction("editor.action.formatDocument")?.run();
    }
  }

  protected run(): void {
    const value = this.editor.getValue();
    this.payload.set(value || '');
  }
}
