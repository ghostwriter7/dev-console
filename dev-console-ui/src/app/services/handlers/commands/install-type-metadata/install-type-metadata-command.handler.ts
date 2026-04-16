import { inject, Injectable } from '@angular/core';
import { INSTALL_TYPE_METADATA_COMMAND } from '../../../../model/commands';
import { CommandOrEventName, InstallTypeMetadataCommand } from '../../../../model/command-or-event.interface';
import { DevConsoleStore } from '../../../../store/dev-console.store';
import { Handler } from '../../handler.interface';

@Injectable({ providedIn: 'root' })
export class InstallTypeMetadataCommandHandler implements Handler<InstallTypeMetadataCommand>{

  private readonly store = inject(DevConsoleStore);

  public canHandle(): CommandOrEventName {
    return INSTALL_TYPE_METADATA_COMMAND;
  }

  public handle({ serviceName, typeMetadata }: InstallTypeMetadataCommand): void {
    this.store.setTypeMetadata(serviceName, typeMetadata);
  }

}
