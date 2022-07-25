import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { KnowledgeCentralService } from '../service/knowledge-central.service';
import { IKnowledgeCentral, KnowledgeCentral } from '../knowledge-central.model';
import { ICandidate } from 'app/entities/candidate/candidate.model';
import { CandidateService } from 'app/entities/candidate/service/candidate.service';

import { KnowledgeCentralUpdateComponent } from './knowledge-central-update.component';

describe('KnowledgeCentral Management Update Component', () => {
  let comp: KnowledgeCentralUpdateComponent;
  let fixture: ComponentFixture<KnowledgeCentralUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let knowledgeCentralService: KnowledgeCentralService;
  let candidateService: CandidateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [KnowledgeCentralUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(KnowledgeCentralUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(KnowledgeCentralUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    knowledgeCentralService = TestBed.inject(KnowledgeCentralService);
    candidateService = TestBed.inject(CandidateService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call candidateTaken query and add missing value', () => {
      const knowledgeCentral: IKnowledgeCentral = { id: 'CBA' };
      const candidateTaken: ICandidate = { id: 'a093e56e-9c99-46ab-a05b-ce6c7240c29a' };
      knowledgeCentral.candidateTaken = candidateTaken;

      const candidateTakenCollection: ICandidate[] = [{ id: 'ffd3e569-5186-4734-b55f-0ade596c494f' }];
      jest.spyOn(candidateService, 'query').mockReturnValue(of(new HttpResponse({ body: candidateTakenCollection })));
      const expectedCollection: ICandidate[] = [candidateTaken, ...candidateTakenCollection];
      jest.spyOn(candidateService, 'addCandidateToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ knowledgeCentral });
      comp.ngOnInit();

      expect(candidateService.query).toHaveBeenCalled();
      expect(candidateService.addCandidateToCollectionIfMissing).toHaveBeenCalledWith(candidateTakenCollection, candidateTaken);
      expect(comp.candidateTakensCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const knowledgeCentral: IKnowledgeCentral = { id: 'CBA' };
      const candidateTaken: ICandidate = { id: '3a364b61-5e38-480e-8ae1-e03d7534083c' };
      knowledgeCentral.candidateTaken = candidateTaken;

      activatedRoute.data = of({ knowledgeCentral });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(knowledgeCentral));
      expect(comp.candidateTakensCollection).toContain(candidateTaken);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<KnowledgeCentral>>();
      const knowledgeCentral = { id: 'ABC' };
      jest.spyOn(knowledgeCentralService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ knowledgeCentral });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: knowledgeCentral }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(knowledgeCentralService.update).toHaveBeenCalledWith(knowledgeCentral);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<KnowledgeCentral>>();
      const knowledgeCentral = new KnowledgeCentral();
      jest.spyOn(knowledgeCentralService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ knowledgeCentral });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: knowledgeCentral }));
      saveSubject.complete();

      // THEN
      expect(knowledgeCentralService.create).toHaveBeenCalledWith(knowledgeCentral);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<KnowledgeCentral>>();
      const knowledgeCentral = { id: 'ABC' };
      jest.spyOn(knowledgeCentralService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ knowledgeCentral });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(knowledgeCentralService.update).toHaveBeenCalledWith(knowledgeCentral);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackCandidateById', () => {
      it('Should return tracked Candidate primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackCandidateById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
