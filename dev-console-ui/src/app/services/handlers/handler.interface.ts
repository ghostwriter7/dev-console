import { CommandOrEvent, CommandOrEventName } from '../../model/command-or-event.interface';

export interface Handler<T extends CommandOrEvent> {
  handle(commandOrEvent: T): void;

  canHandle(): CommandOrEventName;
}
