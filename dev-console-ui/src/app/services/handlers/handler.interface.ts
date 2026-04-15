import { Commands } from '../../model/commands';
import { Events } from '../../model/events';
import { CommandOrEvent } from '../../model/command-or-event.interface';

export interface Handler<T extends CommandOrEvent> {
  handle(commandOrEvent: T): void;

  canHandle(): CommandOrEventName;
}
