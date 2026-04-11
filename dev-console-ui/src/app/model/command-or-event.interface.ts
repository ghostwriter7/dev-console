import { CLIENT_CONNECT_COMMAND, EXECUTE_SCRIPT_COMMAND, SERVER_CONNECT_COMMAND } from '../commands';
import { AVAILABLE_SERVICES_CHANGED_EVENT, SCRIPT_EXECUTED_EVENT, SERVER_CONNECTED_EVENT } from '../events';

export type CommandOrEvent =
  ServerConnectCommand
  | ClientConnectCommand
  | ExecuteScriptCommand
  | ScriptExecutedEvent
  | ServerConnectedEvent
  | AvailableServicesChangedEvent;

export const isCommandOrEvent = (input: unknown): input is CommandOrEvent =>
  typeof input === 'object' && input !== null && 'type' in input;

export interface BaseCommandOrEvent {
  type: string;
}

export interface ServerConnectCommand extends BaseCommandOrEvent {
  type: typeof SERVER_CONNECT_COMMAND;
  serviceName: string;
}

export interface ClientConnectCommand extends BaseCommandOrEvent {
  type: typeof CLIENT_CONNECT_COMMAND;
}

export interface ExecuteScriptCommand extends BaseCommandOrEvent {
  type: typeof EXECUTE_SCRIPT_COMMAND;
  uuid: string;
  targetServiceName: string;
  script: string;
}

export interface ScriptExecutedEvent extends BaseCommandOrEvent {
  type: typeof SCRIPT_EXECUTED_EVENT;
  uuid: string;
  scriptResult: SuccessResult | ErrorResult;
}

export interface SuccessResult {
  result: string;
  stdOut: string;
}

export interface ErrorResult {
  error: string;
  stackTrace: string;
}

export interface ServerConnectedEvent extends BaseCommandOrEvent {
  type: typeof SERVER_CONNECTED_EVENT;
  serviceName: string;
}

export interface AvailableServicesChangedEvent extends BaseCommandOrEvent {
  type: typeof AVAILABLE_SERVICES_CHANGED_EVENT;
  services: string[];
}
