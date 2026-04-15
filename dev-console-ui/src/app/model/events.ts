export const MESSAGE_RECEIVED = 'messageReceived';
export const SCRIPT_EXECUTED_EVENT = 'scriptExecutedEvent'
export const SERVER_CONNECTED_EVENT = 'serverConnectedEvent'
export const AVAILABLE_SERVICES_CHANGED_EVENT = 'availableServicesChangedEvent'

export type Events =
  typeof MESSAGE_RECEIVED
  | typeof SCRIPT_EXECUTED_EVENT
  | typeof SERVER_CONNECTED_EVENT
  | typeof AVAILABLE_SERVICES_CHANGED_EVENT
