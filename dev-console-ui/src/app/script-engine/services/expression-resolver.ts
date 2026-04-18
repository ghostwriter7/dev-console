import { ScriptContext } from '../model/script-context';
import { SymbolEntry } from '../model/symbol-entry.interface';
import { TypeMetadata, UnknownTypeInfo } from '../model/type-info.interface';
import { SymbolResolver } from './symbol-resolver';

export class ExpressionResolver {
  constructor(private readonly symbolResolver: SymbolResolver) {
  }

  public async resolveExpression(expression: string, context: ScriptContext, localVariables: SymbolEntry[]): Promise<TypeMetadata> {
    const parts = expression.split('.').map((part) => part.trim()).reverse();

    const root = parts.pop();
    if (!root) return UnknownTypeInfo;

    let type = await this.symbolResolver.resolve(root, localVariables, context.service);

    while (type !== null && parts.length) {
      const part = parts.pop();
      if (!part) return UnknownTypeInfo;

      const methodName = part.split('(')[0];
      const matchingMethod = type.methods.find(({ name }) => name === methodName);

      if (!matchingMethod) {
        return UnknownTypeInfo;
      }

      type = await this.symbolResolver.resolveTypeInfo(matchingMethod.returnType, context.service);
    }

    return type ?? UnknownTypeInfo;
  }
}
