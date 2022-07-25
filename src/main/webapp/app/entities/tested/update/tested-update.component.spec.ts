import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TestedService } from '../service/tested.service';
import { ITested, Tested } from '../tested.model';

import { TestedUpdateComponent } from './tested-update.component';

describe('Tested Management Update Component', () => {
  let comp: TestedUpdateComponent;
  let fixture: ComponentFixture<TestedUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let testedService: TestedService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TestedUpdateComponent],
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
      .overrideTemplate(TestedUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TestedUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    testedService = TestBed.inject(TestedService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const tested: ITested = { id: 'CBA' };

      activatedRoute.data = of({ tested });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(tested));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tested>>();
      const tested = { id: 'ABC' };
      jest.spyOn(testedService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tested });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tested }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(testedService.update).toHaveBeenCalledWith(tested);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tested>>();
      const tested = new Tested();
      jest.spyOn(testedService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tested });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tested }));
      saveSubject.complete();

      // THEN
      expect(testedService.create).toHaveBeenCalledWith(tested);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tested>>();
      const tested = { id: 'ABC' };
      jest.spyOn(testedService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tested });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(testedService.update).toHaveBeenCalledWith(tested);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
