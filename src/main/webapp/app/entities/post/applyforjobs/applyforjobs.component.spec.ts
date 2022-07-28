import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApplyforjobsComponent } from './applyforjobs.component';

describe('ApplyforjobsComponent', () => {
  let component: ApplyforjobsComponent;
  let fixture: ComponentFixture<ApplyforjobsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ApplyforjobsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplyforjobsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
