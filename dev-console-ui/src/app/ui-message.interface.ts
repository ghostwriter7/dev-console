import {ErrorResult, SuccessResult} from './command-or-event.interface';

export type UiMessage = InputMessage | SuccessMessage | ErrorMessage;

interface Message {
  type: 'input' | 'output' | 'error';
  payload: string | ErrorResult | SuccessResult;
}

export interface ErrorMessage extends Message {
  type: 'error';
  payload: ErrorResult;
}

export interface SuccessMessage extends Message {
  type: 'output';
  payload: SuccessResult;
}

export interface InputMessage extends Message {
  type: 'input';
  payload: string;
}
