import {ApplicationConfig, importProvidersFrom, provideBrowserGlobalErrorListeners} from '@angular/core';
import {provideRouter} from '@angular/router';
import { ClarityModule } from '@clr/angular';

import {routes} from './app.routes';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(ClarityModule, BrowserAnimationsModule),
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
  ]
};
