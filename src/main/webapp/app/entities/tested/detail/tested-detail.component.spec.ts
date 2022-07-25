import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TestedDetailComponent } from './tested-detail.component';

describe('Tested Management Detail Component', () => {
  let comp: TestedDetailComponent;
  let fixture: ComponentFixture<TestedDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TestedDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ tested: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(TestedDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TestedDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load tested on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.tested).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
