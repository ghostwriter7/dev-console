import { Component, ElementRef, input, OnInit, viewChild, viewChildren } from '@angular/core';
import {
  ClrComboboxModule,
  ClrCommonFormsModule,
  ClrTabLink,
  ClrTabsModule,
} from '@clr/angular';
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
  private readonly tabLinks = viewChildren(ClrTabLink);
  public ngOnInit(): void {
    const activeTab = this.payload().result ? 'result' : 'stdOut';

    this.editor = monaco.editor.create(this.editorContainer().nativeElement, {
      language: 'json',
      readOnly: true,
      theme: 'vs-dark',
      automaticLayout: true,
      lineNumbers: 'on',
      value: this.payload()[activeTab],
    });

    const activeTabIndex = activeTab === 'result' ? 0 : 1;
    this.tabLinks().at(activeTabIndex)?.activate();
  }

  protected renderResult(): void {
    this.editor.setValue(this.payload().result || '');
  }

  protected renderStdOut(): void {
    this.editor.setValue(this.payload().stdOut || '');
  }
}
