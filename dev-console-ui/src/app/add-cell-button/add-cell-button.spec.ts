import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddCellButton } from './add-cell-button';

describe('AddCellButton', () => {
  let component: AddCellButton;
  let fixture: ComponentFixture<AddCellButton>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddCellButton],
    }).compileComponents();

    fixture = TestBed.createComponent(AddCellButton);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
