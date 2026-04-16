import {
  CLIENT_CONNECT_COMMAND, Commands,
  EXECUTE_SCRIPT_COMMAND,
  INSTALL_TYPE_METADATA_COMMAND,
  SERVER_CONNECT_COMMAND
} from './commands';
import {
  AVAILABLE_SERVICES_CHANGED_EVENT,
  Events,
  MESSAGE_RECEIVED_EVENT,
  SCRIPT_EXECUTED_EVENT,
  SERVER_CONNECTED_EVENT
} from './events';
import { TypeMetadata } from './type-metadata.interface';

export type CommandOrEvent =
  ServerConnectCommand
  | ClientConnectCommand
  | ExecuteScriptCommand
  | ScriptExecutedEvent
  | ServerConnectedEvent
  | MessageReceivedEvent
  | AvailableServicesChangedEvent
  | InstallTypeMetadataCommand;

export const isCommandOrEvent = (input: unknown): input is CommandOrEvent =>
  typeof input === 'object' && input !== null && 'type' in input;

export interface BaseCommandOrEvent {
  type: CommandOrEventName;
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

export type ScriptResult = SuccessResult | ErrorResult;

export interface ScriptExecutedEvent extends BaseCommandOrEvent {
  type: typeof SCRIPT_EXECUTED_EVENT;
  uuid: string;
  scriptResult: ScriptResult;
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

export interface InstallTypeMetadataCommand extends BaseCommandOrEvent {
  type: typeof INSTALL_TYPE_METADATA_COMMAND,
  serviceName: string;
  typeMetadata: Record<string, TypeMetadata>;
}

export interface MessageReceivedEvent extends BaseCommandOrEvent {
  type: typeof MESSAGE_RECEIVED_EVENT;
  message: string;
}

export type CommandOrEventName = Commands | Events;
