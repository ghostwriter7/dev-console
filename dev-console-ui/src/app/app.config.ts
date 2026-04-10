import { ApplicationConfig, importProvidersFrom, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { ClarityIcons, ClarityModule, codeIcon, cogIcon } from '@clr/angular';

import { routes } from './app.routes';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

ClarityIcons.addIcons(codeIcon, cogIcon);

export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(ClarityModule, BrowserAnimationsModule),
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
  ]
};
