import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutputRenderer } from './output-renderer';

describe('OutputRenderer', () => {
  let component: OutputRenderer;
  let fixture: ComponentFixture<OutputRenderer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OutputRenderer],
    }).compileComponents();

    fixture = TestBed.createComponent(OutputRenderer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
