import { ApplicationConfig, importProvidersFrom, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { asteriskIcon, ClarityIcons, ClarityModule, codeIcon, cogIcon, trashIcon } from '@clr/angular';

import { routes } from './app.routes';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

ClarityIcons.addIcons(codeIcon, cogIcon, trashIcon, asteriskIcon);

export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(ClarityModule, BrowserAnimationsModule),
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
  ]
};
