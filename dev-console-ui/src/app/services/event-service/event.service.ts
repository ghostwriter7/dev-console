import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { CommandOrEvent, CommandOrEventName } from '../../model/command-or-event.interface';

@Injectable({ providedIn: 'root' })
export class EventService {

  private readonly eventSubject = new Subject<{
    type: CommandOrEventName;
    payload: CommandOrEvent | string;
  }>();
  public readonly events$ = this.eventSubject.asObservable();

  public emit(type: CommandOrEventName, payload: CommandOrEvent | string): void {
    this.eventSubject.next({ type, payload });
  }
}
