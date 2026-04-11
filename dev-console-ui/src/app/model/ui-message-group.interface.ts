import { ErrorResult, SuccessResult } from './command-or-event.interface';

export interface UiMessageGroup {
  script: string;
  result?: ErrorResult | SuccessResult;
  uuid: string;
  service: string;
  hasNoOutput: boolean;
}
