import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScriptControl } from './script-control.component';

describe('ScriptInput', () => {
  let component: ScriptControl;
  let fixture: ComponentFixture<ScriptControl>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScriptControl],
    }).compileComponents();

    fixture = TestBed.createComponent(ScriptControl);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
