import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { StatementInfo } from './model/statement-info.interface';
import { TypeInfo, TypeMetadata } from './model/type-info.interface';
import { SymbolEntry } from './model/symbol-entry.interface';

type ServiceName = string;
type ScriptUuid = string;

type ScriptEngineStore = {
  typeMetadata: Record<ServiceName, Record<string, TypeMetadata>>;
  bindings: Record<ServiceName, Record<string, TypeInfo>>;
  localVars: Record<ScriptUuid, SymbolEntry[]>;
  statements: Record<ScriptUuid, StatementInfo[]>;
}

const initialState: ScriptEngineStore = {
  typeMetadata: {},
  bindings: {},
  localVars: {},
  statements: {}
};

export const ScriptEngineStore = signalStore({
    providedIn: 'root'
  },
  withState(initialState),
  withMethods((store) => {

    return {
      getStatementsByUuid: (uuid: string) => store.statements()[uuid],

      insertTypeMetadata: (serviceName: string, typeMetadata: TypeMetadata): void =>
        patchState(store, ({
          typeMetadata: {
            ...store.typeMetadata(),
            [serviceName]: {
              ...store.typeMetadata()[serviceName],
              [typeMetadata.type]: typeMetadata
            }
          }
        })),

      setBindings: (serviceName: string, bindings: SymbolEntry[]): void => {
        const bindingsDictionary = bindings.reduce((dict, binding) => ({ ...dict, [binding.name]: binding.type }), {});
        patchState(store, ({ bindings: { ...store.bindings(), [serviceName]: bindingsDictionary } }));
      },

      setLocalVars: (uuid: string, localVars: SymbolEntry[]): void =>
        patchState(store, ({
          localVars: {
            ...store.localVars(),
            [uuid]: localVars
          }
        })),

      setTypeMetadata: (serviceName: string, typeMetadata: TypeMetadata[]): void => {
        const typeMetadataDictionary = typeMetadata.reduce((dict, typeInfo) => ({
          ...dict,
          [typeInfo.type]: typeInfo
        }), {});
        patchState(store, ({ typeMetadata: { ...store.typeMetadata(), [serviceName]: typeMetadataDictionary } }))
      },

      setStatements: (uuid: string, statements: StatementInfo[]): void => {
        patchState(store, ({
          statements: {
            ...store.statements(),
            [uuid]: statements
          }
        }))
      }
    }
  }));

