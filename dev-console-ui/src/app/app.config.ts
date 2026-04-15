import { ApplicationConfig, importProvidersFrom, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { asteriskIcon, ClarityIcons, ClarityModule, codeIcon, cogIcon, trashIcon } from '@clr/angular';

import { routes } from './app.routes';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AvailableServicesChangedEventHandler } from './services/handlers/events/available-services-changed/available-services-changed-event.handler';
import {
  InstallTypeMetadataCommandHandler
} from './services/handlers/commands/install-type-metadata/install-type-metadata-command.handler';
import { COMMAND_OR_EVENT_HANDLER } from './services/handlers/handler.token';
import { ScriptExecutorService } from './services/handlers/script-executor/script-executor.service';

ClarityIcons.addIcons(codeIcon, cogIcon, trashIcon, asteriskIcon);

export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(ClarityModule, BrowserAnimationsModule),
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    { provide: COMMAND_OR_EVENT_HANDLER, useExisting: AvailableServicesChangedEventHandler, multi: true },
    { provide: COMMAND_OR_EVENT_HANDLER, useExisting: ScriptExecutorService, multi: true },
    { provide: COMMAND_OR_EVENT_HANDLER, useExisting: InstallTypeMetadataCommandHandler, multi: true }
  ]
};
