import { ScriptResult } from './command-or-event.interface';

export interface UiMessageGroup {
  script: string;
  result?: ScriptResult;
  uuid: string;
  service: string;
  hasNoOutput: boolean;
}
