import { Injectable } from '@angular/core';
import { INSTALL_TYPE_METADATA_COMMAND } from '../../../../model/commands';
import { CommandOrEventName, InstallTypeMetadataCommand } from '../../../../model/command-or-event.interface';
import { Handler } from '../../handler.interface';

@Injectable({ providedIn: 'root' })
export class InstallTypeMetadataCommandHandler implements Handler<InstallTypeMetadataCommand>{


  public canHandle(): CommandOrEventName {
    return INSTALL_TYPE_METADATA_COMMAND;
  }

  public handle(commandOrEvent: InstallTypeMetadataCommand): void {
  }

}
