import { describe, expect, it, beforeEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { ScriptEngineStore } from '../script-engine.store';
import { ScriptEngineFacade } from './script-engine.facade';
import { v4 as uuidV4 } from 'uuid';

describe('ScriptEngineFacade', () => {
  let facade: ScriptEngineFacade;
  let store: InstanceType<typeof ScriptEngineStore>;
  beforeEach(() => {
    facade = TestBed.inject(ScriptEngineFacade);
    store = TestBed.inject(ScriptEngineStore);
  });

  it('should create an instance', () => expect(facade).toBeTruthy());

  describe('updateStatements', () => {
    const uuid = uuidV4();
    const service = 'demo';

    it('should update statements', () => {
      store.setTypeMetadata(service, [
        { type: 'User', methods: [{ name: 'getName', returnType: 'String', parameters: [] }] },
        { type: 'UserService', methods: [{ name: 'findById', returnType: 'User', parameters: ['Long'] }] },
        { type: 'String', methods: [] },
      ]);
      store.setBindings(service, [
        { name: 'userService', type: 'UserService' },
      ]);

      const script = `def user = userService.findById(1)
                      def name = user.getName()`;

      facade.updateStatements(uuid, service, script);

      const localVars = facade.getLocalVariables(uuid);
      expect(localVars).toEqual([
        { name: 'user', type: 'User' },
        { name: 'name', type: 'String' },
      ])
    });

  });
});
