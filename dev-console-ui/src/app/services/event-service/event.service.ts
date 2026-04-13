import { Injectable } from '@angular/core';
import { filter, map, Observable, Subject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class EventService {

  private readonly eventSubject = new Subject<{
    type: string;
    payload: unknown
  }>();

  public emit(type: string, payload: unknown): void {
    this.eventSubject.next({ type, payload });
  }

  public getStream$(type: string): Observable<unknown> {
    return this.eventSubject.pipe(
      filter((event) => event.type === type),
      map(({ payload }) => payload)
    );
  }

}
