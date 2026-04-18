export const SERVER_CONNECT_COMMAND = 'serverConnectCommand'
export const CLIENT_CONNECT_COMMAND = 'clientConnectCommand'
export const EXECUTE_SCRIPT_COMMAND = 'executeScriptCommand'
export const INSTALL_TYPE_METADATA_COMMAND = 'installTypeMetadataCommand';
export const COMPUTE_TYPE_METADATA_COMMAND = 'computeTypeMetadataCommand';

export type Commands =
  typeof SERVER_CONNECT_COMMAND
  | typeof CLIENT_CONNECT_COMMAND
  | typeof EXECUTE_SCRIPT_COMMAND
  | typeof INSTALL_TYPE_METADATA_COMMAND
  | typeof COMPUTE_TYPE_METADATA_COMMAND;
