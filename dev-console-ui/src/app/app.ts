import {Component, OnInit, signal} from '@angular/core';
import {MessageRenderer} from './message-renderer/message-renderer.component';
import {ErrorOutput} from './error-output/error-output';

interface Message {
  type: 'input' | 'output' | 'error'
  payload: string;
}

interface SuccessResponse extends Response {
  status: 'success';
  result: string;
}

interface ErrorResponse extends Response {
  status: 'error';
  error: string;
  stackTrace: string;
}

interface Response {
  status: 'success' | 'error';
}

const isErrorResponse = (response: unknown): response is ErrorResponse => (response as ErrorResponse)?.status === 'error';
const isSuccessResponse = (response: unknown): response is SuccessResponse => (response as SuccessResponse)?.status === 'success';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  imports: [
    MessageRenderer,
    ErrorOutput
  ],
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly messages = signal<Message[]>([{
    type: 'input',
    payload: ''
  }]);

  private ws?: WebSocket;

  public ngOnInit(): void {
    this.ws = new WebSocket('wss://localhost:4858/underwriting-rest/v1/dev/console/ws');

    this.ws.addEventListener('message', (event: MessageEvent<string>) => {
      const {data} = event;

      const json = this.tryParse(data);

      if (isSuccessResponse(json)) {
        this.messages.update((messages) => [...messages, {
          type: 'output',
          payload: json.result
        }]);
      } else if (isErrorResponse(json)) {
        this.messages.update((messages) => [...messages, {
          type: 'error',
          payload: json.error
        }]);
      }

    });

    this.ws.addEventListener('open', (event) => {
      console.log('Connected');
    });

    this.ws.addEventListener('close', (event) => {
      console.log('Disconnected');
    });
  }

  protected tryParse(payload: string): object | null {
    try {
      return JSON.parse(payload);
    } catch  {
      return null;
    }
  }

  protected onPayloadChange(payload?: string): void {
    if (this.ws && payload) {
      this.ws.send(payload);
    }
  }
}
