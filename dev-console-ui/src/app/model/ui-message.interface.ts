import { ScriptResult } from './command-or-event.interface';

export interface UiMessage {
  service: string;
  script: string;
  status: 'running' | 'idle';
  result?: ScriptResult;
  uuid: string;
}
