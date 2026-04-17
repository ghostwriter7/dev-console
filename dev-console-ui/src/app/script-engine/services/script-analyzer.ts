import { ScriptContext } from '../model/script-context';
import { ExpressionResolver } from './expression-resolver';
import { StatementInfo } from '../model/statement-info.interface';
import { SymbolEntry } from '../model/symbol-entry.interface';
import { ScriptEngineStore } from '../script-engine.store';

export class ScriptAnalyzer {

  constructor(
    private readonly store: InstanceType<typeof ScriptEngineStore>,
    private readonly expressionResolver: ExpressionResolver,
  ) {
  }

  public rebuildVariables(context: ScriptContext): void {
    const statements: StatementInfo[] = this.store.getStatementsByUuid(context.uuid);
    const declaringStatements = statements.filter(({ raw }) => raw.includes('='));

    const localVariables: SymbolEntry[] = [];

    for (const { raw } of declaringStatements) {
      const [symbol, expression] = raw.split('=');
      const symbolName = symbol.split(' ')[1].trim();
      const type = this.expressionResolver.resolveExpression(expression, context, localVariables);

      localVariables.push({
          name: symbolName,
          type: type.name,
        }
      );
    }

    this.store.setLocalVars(context.uuid, localVariables);
  }
}
