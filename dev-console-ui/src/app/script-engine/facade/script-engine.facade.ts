import { inject, Injectable } from '@angular/core';
import { ScriptContext } from '../model/script-context';
import { SuggestionContext } from '../model/suggestion-context';
import { SuggestionInfo } from '../model/suggestion-info.interface';
import { SymbolEntry } from '../model/symbol-entry.interface';
import { CursorContextResolver } from '../services/cursor-context-resolver';
import { ExpressionResolver } from '../services/expression-resolver';
import { ScriptEngineStore } from '../script-engine.store';
import { ScriptAnalyzer } from '../services/script-analyzer';
import { StatementInfoUpdater } from '../services/statement-info-updater';
import { SuggestionResolver } from '../services/suggestion-resolver';
import { SymbolResolver } from '../services/symbol-resolver';

@Injectable({ providedIn: 'root' })
export class ScriptEngineFacade {
  private readonly scriptEngineStore = inject(ScriptEngineStore);

  private readonly symbolResolver = new SymbolResolver(this.scriptEngineStore);
  private readonly expressionResolver = new ExpressionResolver(this.symbolResolver);
  private readonly statementInfoUpdater = new StatementInfoUpdater(this.scriptEngineStore);
  private readonly scriptAnalyzer = new ScriptAnalyzer(this.scriptEngineStore, this.expressionResolver);
  private readonly cursorContextResolver = new CursorContextResolver();
  private readonly suggestionResolver = new SuggestionResolver(this.cursorContextResolver, this.expressionResolver, this.scriptEngineStore);

  public getSuggestions(script: string, uuid: string, service: string, line: number, column: number): SuggestionInfo[] {
    const statements = this.scriptEngineStore.getStatementsByUuid(uuid);
    if (!statements?.length) return [];

    const context = new SuggestionContext(service, script, uuid, statements, line, column);
    return this.suggestionResolver.resolve(context);
  }

  public updateStatements(uuid: string, service: string, script: string): void {
    const context = new ScriptContext(service, script, uuid);
    this.statementInfoUpdater.update(context);

    this.scriptAnalyzer.rebuildVariables(context);
  }

  public getLocalVariables(uuid: string): SymbolEntry[] {
    return this.scriptEngineStore.varsByScriptUuid()[uuid];
  }
}
