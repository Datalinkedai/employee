import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { InterviewService } from '../service/interview.service';
import { IInterview, Interview } from '../interview.model';
import { ICandidate } from 'app/entities/candidate/candidate.model';
import { CandidateService } from 'app/entities/candidate/service/candidate.service';

import { InterviewUpdateComponent } from './interview-update.component';

describe('Interview Management Update Component', () => {
  let comp: InterviewUpdateComponent;
  let fixture: ComponentFixture<InterviewUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let interviewService: InterviewService;
  let candidateService: CandidateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [InterviewUpdateComponent],
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
      .overrideTemplate(InterviewUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InterviewUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    interviewService = TestBed.inject(InterviewService);
    candidateService = TestBed.inject(CandidateService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call interviewBy query and add missing value', () => {
      const interview: IInterview = { id: 'CBA' };
      const interviewBy: ICandidate = { id: '383c8b92-3783-43ca-a1ea-d5466e889a08' };
      interview.interviewBy = interviewBy;

      const interviewByCollection: ICandidate[] = [{ id: '0128bf06-e848-4474-8550-b7ed49158e46' }];
      jest.spyOn(candidateService, 'query').mockReturnValue(of(new HttpResponse({ body: interviewByCollection })));
      const expectedCollection: ICandidate[] = [interviewBy, ...interviewByCollection];
      jest.spyOn(candidateService, 'addCandidateToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ interview });
      comp.ngOnInit();

      expect(candidateService.query).toHaveBeenCalled();
      expect(candidateService.addCandidateToCollectionIfMissing).toHaveBeenCalledWith(interviewByCollection, interviewBy);
      expect(comp.interviewBiesCollection).toEqual(expectedCollection);
    });

    it('Should call rescheduleApprovedBy query and add missing value', () => {
      const interview: IInterview = { id: 'CBA' };
      const rescheduleApprovedBy: ICandidate = { id: 'd1cb31fd-5bbe-4e6e-8aae-e57e459f8601' };
      interview.rescheduleApprovedBy = rescheduleApprovedBy;

      const rescheduleApprovedByCollection: ICandidate[] = [{ id: 'c321a680-965b-4126-a921-6d4849c029d5' }];
      jest.spyOn(candidateService, 'query').mockReturnValue(of(new HttpResponse({ body: rescheduleApprovedByCollection })));
      const expectedCollection: ICandidate[] = [rescheduleApprovedBy, ...rescheduleApprovedByCollection];
      jest.spyOn(candidateService, 'addCandidateToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ interview });
      comp.ngOnInit();

      expect(candidateService.query).toHaveBeenCalled();
      expect(candidateService.addCandidateToCollectionIfMissing).toHaveBeenCalledWith(rescheduleApprovedByCollection, rescheduleApprovedBy);
      expect(comp.rescheduleApprovedBiesCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const interview: IInterview = { id: 'CBA' };
      const interviewBy: ICandidate = { id: 'deef9e86-c9f7-4d35-9718-11ef77e729d0' };
      interview.interviewBy = interviewBy;
      const rescheduleApprovedBy: ICandidate = { id: 'a218380b-7789-4cdd-92bb-4e257e84121c' };
      interview.rescheduleApprovedBy = rescheduleApprovedBy;

      activatedRoute.data = of({ interview });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(interview));
      expect(comp.interviewBiesCollection).toContain(interviewBy);
      expect(comp.rescheduleApprovedBiesCollection).toContain(rescheduleApprovedBy);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Interview>>();
      const interview = { id: 'ABC' };
      jest.spyOn(interviewService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ interview });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: interview }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(interviewService.update).toHaveBeenCalledWith(interview);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Interview>>();
      const interview = new Interview();
      jest.spyOn(interviewService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ interview });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: interview }));
      saveSubject.complete();

      // THEN
      expect(interviewService.create).toHaveBeenCalledWith(interview);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Interview>>();
      const interview = { id: 'ABC' };
      jest.spyOn(interviewService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ interview });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(interviewService.update).toHaveBeenCalledWith(interview);
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
