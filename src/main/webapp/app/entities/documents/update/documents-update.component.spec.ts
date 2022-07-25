import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DocumentsService } from '../service/documents.service';
import { IDocuments, Documents } from '../documents.model';
import { ICandidate } from 'app/entities/candidate/candidate.model';
import { CandidateService } from 'app/entities/candidate/service/candidate.service';

import { DocumentsUpdateComponent } from './documents-update.component';

describe('Documents Management Update Component', () => {
  let comp: DocumentsUpdateComponent;
  let fixture: ComponentFixture<DocumentsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let documentsService: DocumentsService;
  let candidateService: CandidateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DocumentsUpdateComponent],
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
      .overrideTemplate(DocumentsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DocumentsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    documentsService = TestBed.inject(DocumentsService);
    candidateService = TestBed.inject(CandidateService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call fromCandidate query and add missing value', () => {
      const documents: IDocuments = { id: 'CBA' };
      const fromCandidate: ICandidate = { id: '49ae401a-888d-402e-b66b-8bd1c7ea785e' };
      documents.fromCandidate = fromCandidate;

      const fromCandidateCollection: ICandidate[] = [{ id: 'c729a995-7729-4a7c-a344-10e769bbe81b' }];
      jest.spyOn(candidateService, 'query').mockReturnValue(of(new HttpResponse({ body: fromCandidateCollection })));
      const expectedCollection: ICandidate[] = [fromCandidate, ...fromCandidateCollection];
      jest.spyOn(candidateService, 'addCandidateToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ documents });
      comp.ngOnInit();

      expect(candidateService.query).toHaveBeenCalled();
      expect(candidateService.addCandidateToCollectionIfMissing).toHaveBeenCalledWith(fromCandidateCollection, fromCandidate);
      expect(comp.fromCandidatesCollection).toEqual(expectedCollection);
    });

    it('Should call verifiedBy query and add missing value', () => {
      const documents: IDocuments = { id: 'CBA' };
      const verifiedBy: ICandidate = { id: 'f3d75ae6-3835-4858-b7f0-705c59cdac81' };
      documents.verifiedBy = verifiedBy;

      const verifiedByCollection: ICandidate[] = [{ id: '348a77b1-c093-43d1-ad44-fc7374d00c22' }];
      jest.spyOn(candidateService, 'query').mockReturnValue(of(new HttpResponse({ body: verifiedByCollection })));
      const expectedCollection: ICandidate[] = [verifiedBy, ...verifiedByCollection];
      jest.spyOn(candidateService, 'addCandidateToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ documents });
      comp.ngOnInit();

      expect(candidateService.query).toHaveBeenCalled();
      expect(candidateService.addCandidateToCollectionIfMissing).toHaveBeenCalledWith(verifiedByCollection, verifiedBy);
      expect(comp.verifiedBiesCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const documents: IDocuments = { id: 'CBA' };
      const fromCandidate: ICandidate = { id: 'c68c0fe4-4635-4ab3-b91c-4eb3bda3db95' };
      documents.fromCandidate = fromCandidate;
      const verifiedBy: ICandidate = { id: 'a2b5076d-70e8-4d50-ad90-db066a24191e' };
      documents.verifiedBy = verifiedBy;

      activatedRoute.data = of({ documents });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(documents));
      expect(comp.fromCandidatesCollection).toContain(fromCandidate);
      expect(comp.verifiedBiesCollection).toContain(verifiedBy);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Documents>>();
      const documents = { id: 'ABC' };
      jest.spyOn(documentsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ documents });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: documents }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(documentsService.update).toHaveBeenCalledWith(documents);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Documents>>();
      const documents = new Documents();
      jest.spyOn(documentsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ documents });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: documents }));
      saveSubject.complete();

      // THEN
      expect(documentsService.create).toHaveBeenCalledWith(documents);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Documents>>();
      const documents = { id: 'ABC' };
      jest.spyOn(documentsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ documents });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(documentsService.update).toHaveBeenCalledWith(documents);
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
