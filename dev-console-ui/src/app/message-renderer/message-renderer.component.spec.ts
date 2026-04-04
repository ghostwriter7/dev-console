import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageRenderer } from './message-renderer.component';

describe('ScriptInput', () => {
  let component: MessageRenderer;
  let fixture: ComponentFixture<MessageRenderer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageRenderer],
    }).compileComponents();

    fixture = TestBed.createComponent(MessageRenderer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
