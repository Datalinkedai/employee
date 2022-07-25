import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { KnowledgeService } from '../service/knowledge.service';
import { IKnowledge, Knowledge } from '../knowledge.model';
import { ITested } from 'app/entities/tested/tested.model';
import { TestedService } from 'app/entities/tested/service/tested.service';
import { ICandidate } from 'app/entities/candidate/candidate.model';
import { CandidateService } from 'app/entities/candidate/service/candidate.service';

import { KnowledgeUpdateComponent } from './knowledge-update.component';

describe('Knowledge Management Update Component', () => {
  let comp: KnowledgeUpdateComponent;
  let fixture: ComponentFixture<KnowledgeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let knowledgeService: KnowledgeService;
  let testedService: TestedService;
  let candidateService: CandidateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [KnowledgeUpdateComponent],
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
      .overrideTemplate(KnowledgeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(KnowledgeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    knowledgeService = TestBed.inject(KnowledgeService);
    testedService = TestBed.inject(TestedService);
    candidateService = TestBed.inject(CandidateService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call tests query and add missing value', () => {
      const knowledge: IKnowledge = { id: 'CBA' };
      const tests: ITested = { id: '41ae00af-7782-467c-9b1b-0f9f9a2cbedd' };
      knowledge.tests = tests;

      const testsCollection: ITested[] = [{ id: '4b4a5f65-6aff-46a2-97c9-aefb6df5aaa5' }];
      jest.spyOn(testedService, 'query').mockReturnValue(of(new HttpResponse({ body: testsCollection })));
      const expectedCollection: ITested[] = [tests, ...testsCollection];
      jest.spyOn(testedService, 'addTestedToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ knowledge });
      comp.ngOnInit();

      expect(testedService.query).toHaveBeenCalled();
      expect(testedService.addTestedToCollectionIfMissing).toHaveBeenCalledWith(testsCollection, tests);
      expect(comp.testsCollection).toEqual(expectedCollection);
    });

    it('Should call candidateTaken query and add missing value', () => {
      const knowledge: IKnowledge = { id: 'CBA' };
      const candidateTaken: ICandidate = { id: '311ef9f6-06fa-4edc-b32e-4aefcd4a1efd' };
      knowledge.candidateTaken = candidateTaken;

      const candidateTakenCollection: ICandidate[] = [{ id: '039820a5-5099-4a63-a16c-faf262e8be9c' }];
      jest.spyOn(candidateService, 'query').mockReturnValue(of(new HttpResponse({ body: candidateTakenCollection })));
      const expectedCollection: ICandidate[] = [candidateTaken, ...candidateTakenCollection];
      jest.spyOn(candidateService, 'addCandidateToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ knowledge });
      comp.ngOnInit();

      expect(candidateService.query).toHaveBeenCalled();
      expect(candidateService.addCandidateToCollectionIfMissing).toHaveBeenCalledWith(candidateTakenCollection, candidateTaken);
      expect(comp.candidateTakensCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const knowledge: IKnowledge = { id: 'CBA' };
      const tests: ITested = { id: '9c50a6d8-8385-4fe1-b279-94522c317c02' };
      knowledge.tests = tests;
      const candidateTaken: ICandidate = { id: 'b9e8f509-cfa2-4489-b0a2-33bc0e9cee49' };
      knowledge.candidateTaken = candidateTaken;

      activatedRoute.data = of({ knowledge });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(knowledge));
      expect(comp.testsCollection).toContain(tests);
      expect(comp.candidateTakensCollection).toContain(candidateTaken);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Knowledge>>();
      const knowledge = { id: 'ABC' };
      jest.spyOn(knowledgeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ knowledge });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: knowledge }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(knowledgeService.update).toHaveBeenCalledWith(knowledge);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Knowledge>>();
      const knowledge = new Knowledge();
      jest.spyOn(knowledgeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ knowledge });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: knowledge }));
      saveSubject.complete();

      // THEN
      expect(knowledgeService.create).toHaveBeenCalledWith(knowledge);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Knowledge>>();
      const knowledge = { id: 'ABC' };
      jest.spyOn(knowledgeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ knowledge });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(knowledgeService.update).toHaveBeenCalledWith(knowledge);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackTestedById', () => {
      it('Should return tracked Tested primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackTestedById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackCandidateById', () => {
      it('Should return tracked Candidate primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackCandidateById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
