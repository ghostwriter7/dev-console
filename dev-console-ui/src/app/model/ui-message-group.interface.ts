import { UiMessage } from './ui-message.interface';

export interface UiMessageGroup extends UiMessage {
  hasNoOutput: boolean;
}
