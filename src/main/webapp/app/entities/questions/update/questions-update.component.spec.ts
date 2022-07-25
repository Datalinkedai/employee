import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { QuestionsService } from '../service/questions.service';
import { IQuestions, Questions } from '../questions.model';
import { ITested } from 'app/entities/tested/tested.model';
import { TestedService } from 'app/entities/tested/service/tested.service';

import { QuestionsUpdateComponent } from './questions-update.component';

describe('Questions Management Update Component', () => {
  let comp: QuestionsUpdateComponent;
  let fixture: ComponentFixture<QuestionsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let questionsService: QuestionsService;
  let testedService: TestedService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [QuestionsUpdateComponent],
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
      .overrideTemplate(QuestionsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(QuestionsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    questionsService = TestBed.inject(QuestionsService);
    testedService = TestBed.inject(TestedService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Tested query and add missing value', () => {
      const questions: IQuestions = { id: 'CBA' };
      const tested: ITested = { id: '2ac21e34-991b-4ee5-ae22-4efba91ba226' };
      questions.tested = tested;

      const testedCollection: ITested[] = [{ id: '47194cf1-52d2-4ca5-87ce-7336c16b96a2' }];
      jest.spyOn(testedService, 'query').mockReturnValue(of(new HttpResponse({ body: testedCollection })));
      const additionalTesteds = [tested];
      const expectedCollection: ITested[] = [...additionalTesteds, ...testedCollection];
      jest.spyOn(testedService, 'addTestedToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ questions });
      comp.ngOnInit();

      expect(testedService.query).toHaveBeenCalled();
      expect(testedService.addTestedToCollectionIfMissing).toHaveBeenCalledWith(testedCollection, ...additionalTesteds);
      expect(comp.testedsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const questions: IQuestions = { id: 'CBA' };
      const tested: ITested = { id: '650e89d1-d9ac-4422-9dc1-60f1a8b3380e' };
      questions.tested = tested;

      activatedRoute.data = of({ questions });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(questions));
      expect(comp.testedsSharedCollection).toContain(tested);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Questions>>();
      const questions = { id: 'ABC' };
      jest.spyOn(questionsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ questions });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: questions }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(questionsService.update).toHaveBeenCalledWith(questions);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Questions>>();
      const questions = new Questions();
      jest.spyOn(questionsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ questions });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: questions }));
      saveSubject.complete();

      // THEN
      expect(questionsService.create).toHaveBeenCalledWith(questions);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Questions>>();
      const questions = { id: 'ABC' };
      jest.spyOn(questionsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ questions });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(questionsService.update).toHaveBeenCalledWith(questions);
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
  });
});
