import {Component, input} from '@angular/core';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-error-output',
  imports: [
    FormsModule
  ],
  templateUrl: './error-output.html',
  styleUrl: './error-output.css',
})
export class ErrorOutput {
  public payload = input.required<string>();

}
