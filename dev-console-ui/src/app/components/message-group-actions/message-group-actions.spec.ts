import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageGroupActions } from './message-group-actions';

describe('MessageGroupActions', () => {
  let component: MessageGroupActions;
  let fixture: ComponentFixture<MessageGroupActions>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageGroupActions],
    }).compileComponents();

    fixture = TestBed.createComponent(MessageGroupActions);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
