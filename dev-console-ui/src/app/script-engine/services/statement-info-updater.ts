import { ScriptContext } from '../model/script-context';
import { StatementInfo } from '../model/statement-info.interface';
import { ScriptEngineStore } from '../script-engine.store';

export class StatementInfoUpdater {

  constructor(private readonly store: InstanceType<typeof ScriptEngineStore>) {
  }

  public update({ script, uuid }: ScriptContext): void {
    const statements: StatementInfo[] = script.split('\n')
      .map((line, index) => ({
        line: index + 1,
        raw: line.trim()
      }));

    this.store.setStatements(uuid, statements);
  }
}
