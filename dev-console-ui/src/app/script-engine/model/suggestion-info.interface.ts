import { CompletionType } from '../completion-type.enum';

export interface SuggestionInfo {
  label: string;
  kind: CompletionType;
  insertText: string;
}
