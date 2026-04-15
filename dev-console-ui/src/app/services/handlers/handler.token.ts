import { InjectionToken } from '@angular/core';
import { CommandOrEvent } from '../../model/command-or-event.interface';
import { Handler } from './handler.interface';

export const COMMAND_OR_EVENT_HANDLER = new InjectionToken<Handler<CommandOrEvent>[]>('Handler');
