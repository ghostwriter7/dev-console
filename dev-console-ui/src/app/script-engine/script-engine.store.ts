import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { StatementInfo } from './model/statement-info.interface';
import { TypeInfo } from './model/type-info.interface';
import { SymbolEntry } from './model/symbol-entry.interface';

type ScriptEngineStore = {
  typesByServiceName: Record<string, Record<string, TypeInfo>>;
  bindingsByServiceName: Record<string, Record<string, SymbolEntry>>;
  varsByScriptUuid: Record<string, SymbolEntry[]>;
  statementsByScriptUuid: Record<string, StatementInfo[]>;
}

const initialState: ScriptEngineStore = {
  typesByServiceName: {
    demo: {
      String: {
        name: 'String',
        methodsByName: {
          startsWith: [{
            parameters: ['prefix'],
            returnType: 'boolean',
          }],
        },
      },
      UserService: {
        name: 'UserService',
        methodsByName: {
          findById: [{
            parameters: ['id'],
            returnType: 'User',
          }],
          findAll: [{
            parameters: [],
            returnType: 'List<User>',
          }],
        },
      },
      User: {
        name: 'User',
        methodsByName: {
          getName: [{
            parameters: [],
            returnType: 'String',
          }],
          getOrders: [{
            parameters: [],
            returnType: 'List<Order>',
          }],
        }
      }
    }
  },
  bindingsByServiceName: {
    'demo': {
      userService: { name: 'userService', type: 'UserService' },
    }
  },
  varsByScriptUuid: {},
  statementsByScriptUuid: {}
};

export const ScriptEngineStore = signalStore({
    providedIn: 'root'
  },
  withState(initialState),
  withMethods((store) => {

    return {
      getStatementsByUuid: (uuid: string) => store.statementsByScriptUuid()[uuid],

      setLocalVars: (uuid: string, localVars: SymbolEntry[]): void =>
        patchState(store, ({
          varsByScriptUuid: {
            ...store.varsByScriptUuid(),
            [uuid]: localVars
          }
        })),

      setStatements: (uuid: string, statements: StatementInfo[]): void => {
        patchState(store, ({
          statementsByScriptUuid: {
            ...store.statementsByScriptUuid(),
            [uuid]: statements
          }
        }))
      }
    }
  }));

