import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IKnowledge, Knowledge } from '../knowledge.model';

import { KnowledgeService } from './knowledge.service';

describe('Knowledge Service', () => {
  let service: KnowledgeService;
  let httpMock: HttpTestingController;
  let elemDefault: IKnowledge;
  let expectedResult: IKnowledge | IKnowledge[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(KnowledgeService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 'AAAAAAA',
      result: 0,
      testTaken: currentDate,
      certificateContentType: 'image/png',
      certificate: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          testTaken: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Knowledge', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
          testTaken: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          testTaken: currentDate,
        },
        returnedFromService
      );

      service.create(new Knowledge()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Knowledge', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          result: 1,
          testTaken: currentDate.format(DATE_TIME_FORMAT),
          certificate: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          testTaken: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Knowledge', () => {
      const patchObject = Object.assign(
        {
          testTaken: currentDate.format(DATE_TIME_FORMAT),
        },
        new Knowledge()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          testTaken: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Knowledge', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          result: 1,
          testTaken: currentDate.format(DATE_TIME_FORMAT),
          certificate: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          testTaken: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Knowledge', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addKnowledgeToCollectionIfMissing', () => {
      it('should add a Knowledge to an empty array', () => {
        const knowledge: IKnowledge = { id: 'ABC' };
        expectedResult = service.addKnowledgeToCollectionIfMissing([], knowledge);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(knowledge);
      });

      it('should not add a Knowledge to an array that contains it', () => {
        const knowledge: IKnowledge = { id: 'ABC' };
        const knowledgeCollection: IKnowledge[] = [
          {
            ...knowledge,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addKnowledgeToCollectionIfMissing(knowledgeCollection, knowledge);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Knowledge to an array that doesn't contain it", () => {
        const knowledge: IKnowledge = { id: 'ABC' };
        const knowledgeCollection: IKnowledge[] = [{ id: 'CBA' }];
        expectedResult = service.addKnowledgeToCollectionIfMissing(knowledgeCollection, knowledge);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(knowledge);
      });

      it('should add only unique Knowledge to an array', () => {
        const knowledgeArray: IKnowledge[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '2df9d6aa-f155-4155-98fb-795a6ea82b83' }];
        const knowledgeCollection: IKnowledge[] = [{ id: 'ABC' }];
        expectedResult = service.addKnowledgeToCollectionIfMissing(knowledgeCollection, ...knowledgeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const knowledge: IKnowledge = { id: 'ABC' };
        const knowledge2: IKnowledge = { id: 'CBA' };
        expectedResult = service.addKnowledgeToCollectionIfMissing([], knowledge, knowledge2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(knowledge);
        expect(expectedResult).toContain(knowledge2);
      });

      it('should accept null and undefined values', () => {
        const knowledge: IKnowledge = { id: 'ABC' };
        expectedResult = service.addKnowledgeToCollectionIfMissing([], null, knowledge, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(knowledge);
      });

      it('should return initial array if no Knowledge is added', () => {
        const knowledgeCollection: IKnowledge[] = [{ id: 'ABC' }];
        expectedResult = service.addKnowledgeToCollectionIfMissing(knowledgeCollection, undefined, null);
        expect(expectedResult).toEqual(knowledgeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
