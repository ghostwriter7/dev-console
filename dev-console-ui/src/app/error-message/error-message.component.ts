import {Component, input} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ClrAlertModule} from '@clr/angular';

@Component({
  selector: 'app-error-message',
  imports: [
    FormsModule,
    ClrAlertModule
  ],
  templateUrl: './error-message.component.html',
  styleUrl: './error-message.component.css',
})
export class ErrorMessage {
  public error = input.required<string>();
  public stackTrace = input.required<string>();

}
