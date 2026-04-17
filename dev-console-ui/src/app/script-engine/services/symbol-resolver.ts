import { SymbolEntry } from '../model/symbol-entry.interface';
import { TypeInfo } from '../model/type-info.interface';
import { ScriptEngineStore } from '../script-engine.store';

export class SymbolResolver {

  constructor(private readonly scriptEngineStore: InstanceType<typeof ScriptEngineStore>,) {
  }


  public resolve(symbol: string, localVars: SymbolEntry[], service: string): TypeInfo | null {
    const localSymbol = localVars.find(({ name }) => name === symbol);
    if (localSymbol) return this.resolveTypeInfo(localSymbol.type, service);

    const bindings = this.scriptEngineStore.bindingsByServiceName()[service];
    if (bindings[symbol]) return this.resolveTypeInfo(bindings[symbol].type, service);

    return null;
  }

  public resolveTypeInfo(type: string, service: string): TypeInfo {
    const types = this.scriptEngineStore.typesByServiceName()[service];
    return types[type];
  }
}
