import { inject, Injectable } from '@angular/core';
import { INSTALL_TYPE_METADATA_COMMAND } from '../../../../model/commands';
import { CommandOrEventName, InstallTypeMetadataCommand } from '../../../../model/command-or-event.interface';
import { ScriptEngineStore } from '../../../../script-engine/script-engine.store';
import { Handler } from '../../handler.interface';

@Injectable({ providedIn: 'root' })
export class InstallTypeMetadataCommandHandler implements Handler<InstallTypeMetadataCommand>{

  private readonly store = inject(ScriptEngineStore);

  public canHandle(): CommandOrEventName {
    return INSTALL_TYPE_METADATA_COMMAND;
  }

  public handle({ serviceName, typeMetadata, symbols }: InstallTypeMetadataCommand): void {
    this.store.setTypeMetadata(serviceName, typeMetadata);
    this.store.setBindings(serviceName, symbols);
  }

}
