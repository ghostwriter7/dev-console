import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';

(window as any).MonacoEnvironment = {
  getWorkerUrl: (moduleId: string, label: string) => {
    if (label === 'json') {
      return './assets/monaco/esm/vs/language/json/json.worker.js'
    }

    if (label === 'java') {
      return './assets/monaco/esm/vs/basic-languages/java/java.js'
    }

    return './assets/monaco/esm/vs/editor/editor.worker.js'
  },
}

bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));
