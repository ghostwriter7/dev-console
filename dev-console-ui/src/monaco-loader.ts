(self as any).MonacoEnvironment = {
  getWorker: function (_: any, label: string) {
    switch (label) {
      case 'json':
        return new Worker(
          './assets/monaco-editor/esm/vs/language/json/json.worker.js',
          { type: 'classic' }
        );
      case 'css':
      case 'scss':
      case 'less':
        return new Worker(
          './assets/monaco-editor/esm/vs/language/css/css.worker.js',
          { type: 'classic' }
        );
      case 'html':
      case 'handlebars':
      case 'razor':
        return new Worker(
          './assets/monaco-editor/esm/vs/language/html/html.worker.js',
          { type: 'classic' }
        );
      case 'typescript':
      case 'javascript':
        return new Worker(
          './assets/monaco-editor/esm/vs/language/typescript/ts.worker.js',
          { type: 'classic' }
        );
      case 'java':
        return new Worker(
          './assets/monaco-editor/esm/vs/java-CI4ZMsH9.js',
          { type: 'classic' }
        );
      default:
        return new Worker(
          './assets/monaco-editor/esm/vs/editor/editor.worker.js',
          { type: 'classic' }
        );
    }
  }
};
