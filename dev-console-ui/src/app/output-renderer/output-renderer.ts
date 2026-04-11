import { Component, effect, ElementRef, input, OnInit, viewChild, viewChildren } from '@angular/core';
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

  constructor() {
    effect(() => {
      const payload = this.payload();

      if (!payload) return;

      const activeTab = payload.result ? 'result' : 'stdOut';
      const activeTabIndex = activeTab === 'result' ? 0 : 1;
      this.tabLinks().at(activeTabIndex)?.activate();
      if (activeTab === 'result') {
        this.renderResult();
      } else {
        this.renderStdOut();
      }
    });
  }

  public ngOnInit(): void {
    this.editor = monaco.editor.create(this.editorContainer().nativeElement, {
      readOnly: true,
      theme: 'vs-dark',
      automaticLayout: true,
      lineNumbers: 'on',
      model: null,
    });
  }

  protected renderResult(): void {
    const {result} = this.payload();
    let formattedJson: string | undefined;

    try {
      formattedJson = JSON.stringify(JSON.parse(result), null, 2);
    } catch (e) {
      console.error(`Couldn't pretty-print the output.`, e);
    }

    const language = formattedJson ? 'json' : 'text';

    this.editor.setModel(monaco.editor.createModel(formattedJson || result || '', language));
  }

  protected renderStdOut(): void {
    this.editor.setModel(monaco.editor.createModel(this.payload().stdOut || '', 'text'));
  }
}
