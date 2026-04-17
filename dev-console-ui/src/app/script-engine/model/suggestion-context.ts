import { ScriptContext } from './script-context';
import { StatementInfo } from './statement-info.interface';

export class SuggestionContext extends ScriptContext {
  constructor(
   service: string,
   script: string,
   uuid: string,
   public readonly statements: StatementInfo[],
   public readonly line: number,
   public readonly column: number,
  ) {
    super(service, script, uuid);
  }
}
