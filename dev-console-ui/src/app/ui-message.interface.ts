import {ErrorResult, SuccessResult} from './command-or-event.interface';

export type UiMessage = InputMessage | SuccessMessage | ErrorMessage;

interface Message {
  type: 'input' | 'output' | 'error';
  payload: string | ErrorResult | SuccessResult;
  uuid: string;
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

export const isInputMessage = (message: UiMessage): message is InputMessage => message.type === 'input';
export const isOutputMessage = (message: UiMessage): message is SuccessMessage => message.type === 'output';
export const isErrorMessage = (message: UiMessage): message is ErrorMessage => message.type === 'error';
