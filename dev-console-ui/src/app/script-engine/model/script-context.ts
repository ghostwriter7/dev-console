export class ScriptContext {
  constructor(public readonly service: string,
              public readonly script: string,
              public readonly uuid: string) {
  }
}
