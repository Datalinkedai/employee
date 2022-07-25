import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OptionsDetailComponent } from './options-detail.component';

describe('Options Management Detail Component', () => {
  let comp: OptionsDetailComponent;
  let fixture: ComponentFixture<OptionsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OptionsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ options: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(OptionsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OptionsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load options on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.options).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
