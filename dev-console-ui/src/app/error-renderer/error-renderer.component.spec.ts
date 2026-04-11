import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorRenderer } from './error-renderer.component';

describe('ErrorOutput', () => {
  let component: ErrorRenderer;
  let fixture: ComponentFixture<ErrorRenderer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ErrorRenderer],
    }).compileComponents();

    fixture = TestBed.createComponent(ErrorRenderer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
