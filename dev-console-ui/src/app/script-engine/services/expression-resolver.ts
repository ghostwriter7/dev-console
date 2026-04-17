import { ScriptContext } from '../model/script-context';
import { SymbolEntry } from '../model/symbol-entry.interface';
import { TypeInfo, UnknownTypeInfo } from '../model/type-info.interface';
import { SymbolResolver } from './symbol-resolver';

export class ExpressionResolver {
  constructor(private readonly symbolResolver: SymbolResolver) {
  }

  public resolveExpression(expression: string, context: ScriptContext, localVariables: SymbolEntry[]): TypeInfo {
    const parts = expression.split('.').map((part) => part.trim()).reverse();

    const root = parts.pop();
    if (!root) return UnknownTypeInfo;

    let type = this.symbolResolver.resolve(root, localVariables, context.service);

    while (type !== null && parts.length) {
      const part = parts.pop();
      if (!part) return UnknownTypeInfo;

      const methodName = part.split('(')[0];
      const matchingMethod = type.methodsByName[methodName]?.[0];

      if (!matchingMethod) {
        return UnknownTypeInfo;
      }

      type = this.symbolResolver.resolveTypeInfo(matchingMethod.returnType, context.service);
    }

    return type ?? UnknownTypeInfo;
  }
}
