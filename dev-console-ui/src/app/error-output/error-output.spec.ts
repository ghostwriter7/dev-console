import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorOutput } from './error-output';

describe('ErrorOutput', () => {
  let component: ErrorOutput;
  let fixture: ComponentFixture<ErrorOutput>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ErrorOutput],
    }).compileComponents();

    fixture = TestBed.createComponent(ErrorOutput);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
