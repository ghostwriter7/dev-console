import { Component, ElementRef, input, model, OnInit, viewChild} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import * as monaco from 'monaco-editor'

@Component({
  selector: 'app-message-renderer',
  imports: [
    FormsModule,
    ReactiveFormsModule,
  ],
  templateUrl: './message-renderer.component.html',
  styleUrl: './message-renderer.component.css',
})
export class MessageRenderer implements OnInit {
  public readOnly = input(false);
  public language = input('java');
  public payload = model.required<string>();

  private readonly editorContainer = viewChild.required<ElementRef<HTMLDivElement>>('editor');

  private editor!: monaco.editor.IStandaloneCodeEditor;

  public ngOnInit(): void {
    this.editor = monaco.editor.create(this.editorContainer().nativeElement, {
      language: this.language(),
      readOnly: this.readOnly(),
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
