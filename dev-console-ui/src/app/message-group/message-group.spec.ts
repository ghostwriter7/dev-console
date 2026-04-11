import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageGroup } from './message-group';

describe('MessageGroup', () => {
  let component: MessageGroup;
  let fixture: ComponentFixture<MessageGroup>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageGroup],
    }).compileComponents();

    fixture = TestBed.createComponent(MessageGroup);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
