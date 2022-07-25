import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { KnowledgeCentralDetailComponent } from './knowledge-central-detail.component';

describe('KnowledgeCentral Management Detail Component', () => {
  let comp: KnowledgeCentralDetailComponent;
  let fixture: ComponentFixture<KnowledgeCentralDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [KnowledgeCentralDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ knowledgeCentral: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(KnowledgeCentralDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(KnowledgeCentralDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load knowledgeCentral on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.knowledgeCentral).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
