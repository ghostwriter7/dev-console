import { Component, computed, ElementRef, input, OnInit, signal, viewChild } from '@angular/core';
import { ClrComboboxModule, ClrCommonFormsModule, ClrTabsModule } from '@clr/angular';
import * as monaco from 'monaco-editor';
import { SuccessResult } from '../command-or-event.interface';

@Component({
  selector: 'app-output-renderer',
  imports: [
    ClrComboboxModule,
    ClrCommonFormsModule,
    ClrTabsModule
  ],
  templateUrl: './output-renderer.html',
  styleUrl: './output-renderer.css',
})
export class OutputRenderer implements OnInit {
  public payload = input.required<SuccessResult>();

  protected readonly editorContainer = viewChild.required<ElementRef<HTMLDivElement>>('editor');

  private editor!: monaco.editor.IStandaloneCodeEditor;

  public ngOnInit(): void {
    this.editor = monaco.editor.create(this.editorContainer().nativeElement, {
      language: 'json',
      readOnly: true,
      theme: 'vs-dark',
      automaticLayout: true,
      lineNumbers: 'on',
      value: this.payload().result,
    });
  }

  protected renderResult(): void {
    this.editor.setValue(this.payload().result);
  }

  protected renderStdOut(): void {
    this.editor.setValue(this.payload().stdOut || '');
  }
}
