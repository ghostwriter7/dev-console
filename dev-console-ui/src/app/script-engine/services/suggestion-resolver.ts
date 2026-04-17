import { CursorContext } from '../cursor-context.enum';
import { SuggestionContext } from '../model/suggestion-context';
import { MethodInfo, TypeInfo, UnknownTypeInfo } from '../model/type-info.interface';
import { ScriptEngineStore } from '../script-engine.store';
import { CursorContextResolver } from './cursor-context-resolver';
import { SuggestionInfo } from '../model/suggestion-info.interface';
import { ExpressionResolver } from './expression-resolver';
import * as monaco from 'monaco-editor';

export class SuggestionResolver {

  constructor(
    private readonly cursorContextResolver: CursorContextResolver,
    private readonly expressionResolver: ExpressionResolver,
    private readonly scriptEngineStore: InstanceType<typeof ScriptEngineStore>,
  ) {
  }

  public resolve(context: SuggestionContext): SuggestionInfo[] {
    const { statements, line, column } = context;
    const statementWithCursor = statements[line - 1];

    if (!statementWithCursor) return [];

    const cursorContext: CursorContext = this.cursorContextResolver.resolve(statementWithCursor.raw, column);

    switch (cursorContext) {
      case CursorContext.ROOT_SYMBOL: {
        const bindings = this.scriptEngineStore.bindingsByServiceName()[context.service];
        const localVariables = this.scriptEngineStore.varsByScriptUuid()[context.uuid];

        const statement = this.getExpressionFromStatement(statementWithCursor.raw);

        return [...Object.keys(bindings), ...localVariables.map(({ name }) => name)]
          .filter((symbolName) => symbolName.startsWith(statement) && symbolName !== statement)
          .map((symbolName) => ({
            label: symbolName,
            kind: monaco.languages.CompletionItemKind.Variable,
            insertText: symbolName
          }));
      }
      case CursorContext.INSIDE_CALL:
        // return Object.entries(typeInfo.methodsByName)
        //   .filter(([methodName, methodInfos]) => methodName.startsWith(prefix))
        //   .flatMap(([methodName, methodInfos]) => methodInfos.map((methodInfo) => ()));
        return []; // return suggestions matching a type of the current parameter
      case CursorContext.MEMBER_ACCESS: {
        const lastDotIndex = statementWithCursor.raw.lastIndexOf('.');
        const expression = this.getExpressionFromStatement(statementWithCursor.raw.substring(0, lastDotIndex));
        const localVariables = this.scriptEngineStore.varsByScriptUuid()[context.uuid];
        const typeInfo: TypeInfo = this.expressionResolver.resolveExpression(expression, context, localVariables)

        if (typeInfo === UnknownTypeInfo) return [];

        const prefix = statementWithCursor.raw.substring(lastDotIndex + 1);

        return Object.entries(this.getFilterMethods(typeInfo.methodsByName, prefix))
          .flatMap(([methodName, methodInfos]) => methodInfos.map((methodInfo) => ({
            label: `${methodName}(${methodInfo.parameters.join(', ')})`,
            kind: monaco.languages.CompletionItemKind.Method,
            insertText: methodInfo.parameters.length ? `${methodName}(` : `${methodName}()`,
          })));
      }
    }
    return [];
  }

  private getFilterMethods(methods: Record<string, MethodInfo[]>, prefix: string): Record<string, MethodInfo[]> {
    if (!prefix) return methods;

    return Object.fromEntries(
      Object.entries(methods)
        .filter(([methodName, methodInfos]) => methodName.startsWith(prefix) && methodName !== prefix)
    );
  }

  private getExpressionFromStatement(statement: string): string {
    if (statement.includes('=')) {
      return statement.substring(statement.indexOf('=') + 1).trim();
    }
    return statement;
  }
}
