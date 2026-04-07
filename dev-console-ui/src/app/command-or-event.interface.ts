export interface CommandOrEvent {
  type: 'serverConnectCommand' | 'clientConnectCommand' | 'executeScriptCommand' | 'scriptExecutedEvent' | 'serverConnectedEvent' | 'availableServicesChangedEvent';
}

export interface ServerConnectCommand extends CommandOrEvent {
  type: 'serverConnectCommand';
  serviceName: string;
}

export interface ClientConnectCommand extends CommandOrEvent {
  type: 'clientConnectCommand';
}

export interface ExecuteScriptCommand extends CommandOrEvent {
  type: 'executeScriptCommand';
  uuid: string;
  targetServiceName: string;
  script: string;
}

export interface ScriptExecutedEvent<T = ScriptResult> extends CommandOrEvent {
  type: 'scriptExecutedEvent';
  uuid: string;
  scriptResult: T;
}

export interface ScriptResult {
}

export interface SuccessResult extends ScriptResult {
  result: string;
  stdOut: string;
}

export interface ErrorResult extends ScriptResult {
  error: string;
  stackTrace: string;
}

export interface ServerConnectedEvent extends CommandOrEvent {
  type: 'serverConnectedEvent';
  serviceName: string;
}

export interface AvailableServicesChangedEvent extends CommandOrEvent {
  type: 'availableServicesChangedEvent';
  services: string[];
}
