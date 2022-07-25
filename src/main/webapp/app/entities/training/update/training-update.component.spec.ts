import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TrainingService } from '../service/training.service';
import { ITraining, Training } from '../training.model';
import { ICandidate } from 'app/entities/candidate/candidate.model';
import { CandidateService } from 'app/entities/candidate/service/candidate.service';

import { TrainingUpdateComponent } from './training-update.component';

describe('Training Management Update Component', () => {
  let comp: TrainingUpdateComponent;
  let fixture: ComponentFixture<TrainingUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let trainingService: TrainingService;
  let candidateService: CandidateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TrainingUpdateComponent],
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
      .overrideTemplate(TrainingUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TrainingUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    trainingService = TestBed.inject(TrainingService);
    candidateService = TestBed.inject(CandidateService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Candidate query and add missing value', () => {
      const training: ITraining = { id: 'CBA' };
      const candidateList: ICandidate = { id: 'c5277772-8219-413f-817f-c5f67179a9ac' };
      training.candidateList = candidateList;

      const candidateCollection: ICandidate[] = [{ id: '3d827b6a-35d9-4330-88a5-c62c65600777' }];
      jest.spyOn(candidateService, 'query').mockReturnValue(of(new HttpResponse({ body: candidateCollection })));
      const additionalCandidates = [candidateList];
      const expectedCollection: ICandidate[] = [...additionalCandidates, ...candidateCollection];
      jest.spyOn(candidateService, 'addCandidateToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ training });
      comp.ngOnInit();

      expect(candidateService.query).toHaveBeenCalled();
      expect(candidateService.addCandidateToCollectionIfMissing).toHaveBeenCalledWith(candidateCollection, ...additionalCandidates);
      expect(comp.candidatesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const training: ITraining = { id: 'CBA' };
      const candidateList: ICandidate = { id: '3d06f551-f49a-4e4b-80e1-1eaf6d9b8499' };
      training.candidateList = candidateList;

      activatedRoute.data = of({ training });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(training));
      expect(comp.candidatesSharedCollection).toContain(candidateList);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Training>>();
      const training = { id: 'ABC' };
      jest.spyOn(trainingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ training });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: training }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(trainingService.update).toHaveBeenCalledWith(training);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Training>>();
      const training = new Training();
      jest.spyOn(trainingService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ training });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: training }));
      saveSubject.complete();

      // THEN
      expect(trainingService.create).toHaveBeenCalledWith(training);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Training>>();
      const training = { id: 'ABC' };
      jest.spyOn(trainingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ training });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(trainingService.update).toHaveBeenCalledWith(training);
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
