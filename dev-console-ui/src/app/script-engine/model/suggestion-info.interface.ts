import * as monaco from 'monaco-editor';

export interface SuggestionInfo {
  label: string;
  kind: monaco.languages.CompletionItemKind;
  insertText: string;
}
