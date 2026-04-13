import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageGroupList } from './message-group-list';

describe('MessageGroupList', () => {
  let component: MessageGroupList;
  let fixture: ComponentFixture<MessageGroupList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageGroupList],
    }).compileComponents();

    fixture = TestBed.createComponent(MessageGroupList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
