import {Component, input} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ClrAlertModule} from '@clr/angular';

@Component({
  selector: 'app-error-renderer',
  imports: [
    FormsModule,
    ClrAlertModule
  ],
  templateUrl: './error-renderer.component.html',
  styleUrl: './error-renderer.component.css',
})
export class ErrorRenderer {
  public error = input.required<string>();
  public stackTrace = input.required<string>();

}
