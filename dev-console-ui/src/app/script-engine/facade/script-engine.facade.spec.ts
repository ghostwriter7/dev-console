import { describe, expect, it, beforeEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { ScriptEngineFacade } from './script-engine.facade';
import { v4 as uuidV4 } from 'uuid';

describe('ScriptEngineFacade', () => {
  let facade: ScriptEngineFacade;

  beforeEach(() => {
    facade = TestBed.inject(ScriptEngineFacade);
  });

  it('should create an instance', () => expect(facade).toBeTruthy());

  describe('updateStatements', () => {
    const uuid = uuidV4();
    const service = 'demo';

    it('should update statements', () => {
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
