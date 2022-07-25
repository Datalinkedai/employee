import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OptionsService } from '../service/options.service';
import { IOptions, Options } from '../options.model';
import { IQuestions } from 'app/entities/questions/questions.model';
import { QuestionsService } from 'app/entities/questions/service/questions.service';

import { OptionsUpdateComponent } from './options-update.component';

describe('Options Management Update Component', () => {
  let comp: OptionsUpdateComponent;
  let fixture: ComponentFixture<OptionsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let optionsService: OptionsService;
  let questionsService: QuestionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OptionsUpdateComponent],
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
      .overrideTemplate(OptionsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OptionsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    optionsService = TestBed.inject(OptionsService);
    questionsService = TestBed.inject(QuestionsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Questions query and add missing value', () => {
      const options: IOptions = { id: 'CBA' };
      const questions: IQuestions = { id: '4fe1b4d5-d42d-4701-af2a-ef4ecfacbcce' };
      options.questions = questions;

      const questionsCollection: IQuestions[] = [{ id: '934da18b-3b0d-46d2-a812-524d6bdd5cd2' }];
      jest.spyOn(questionsService, 'query').mockReturnValue(of(new HttpResponse({ body: questionsCollection })));
      const additionalQuestions = [questions];
      const expectedCollection: IQuestions[] = [...additionalQuestions, ...questionsCollection];
      jest.spyOn(questionsService, 'addQuestionsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ options });
      comp.ngOnInit();

      expect(questionsService.query).toHaveBeenCalled();
      expect(questionsService.addQuestionsToCollectionIfMissing).toHaveBeenCalledWith(questionsCollection, ...additionalQuestions);
      expect(comp.questionsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const options: IOptions = { id: 'CBA' };
      const questions: IQuestions = { id: 'b5650913-6512-4643-a505-f66628cc68d4' };
      options.questions = questions;

      activatedRoute.data = of({ options });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(options));
      expect(comp.questionsSharedCollection).toContain(questions);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Options>>();
      const options = { id: 'ABC' };
      jest.spyOn(optionsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ options });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: options }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(optionsService.update).toHaveBeenCalledWith(options);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Options>>();
      const options = new Options();
      jest.spyOn(optionsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ options });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: options }));
      saveSubject.complete();

      // THEN
      expect(optionsService.create).toHaveBeenCalledWith(options);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Options>>();
      const options = { id: 'ABC' };
      jest.spyOn(optionsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ options });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(optionsService.update).toHaveBeenCalledWith(options);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackQuestionsById', () => {
      it('Should return tracked Questions primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackQuestionsById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
