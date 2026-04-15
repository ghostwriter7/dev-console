import { CommandOrEvent } from '../../model/command-or-event.interface';

export interface Handler<T extends CommandOrEvent> {
  handle(commandOrEvent: T): void;

  canHandle(commandOrEvent: CommandOrEvent): boolean;
}
