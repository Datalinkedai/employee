import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IKnowledgeCentral, KnowledgeCentral } from '../knowledge-central.model';

import { KnowledgeCentralService } from './knowledge-central.service';

describe('KnowledgeCentral Service', () => {
  let service: KnowledgeCentralService;
  let httpMock: HttpTestingController;
  let elemDefault: IKnowledgeCentral;
  let expectedResult: IKnowledgeCentral | IKnowledgeCentral[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(KnowledgeCentralService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      averageResult: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a KnowledgeCentral', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new KnowledgeCentral()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a KnowledgeCentral', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          averageResult: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a KnowledgeCentral', () => {
      const patchObject = Object.assign({}, new KnowledgeCentral());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of KnowledgeCentral', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          averageResult: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a KnowledgeCentral', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addKnowledgeCentralToCollectionIfMissing', () => {
      it('should add a KnowledgeCentral to an empty array', () => {
        const knowledgeCentral: IKnowledgeCentral = { id: 'ABC' };
        expectedResult = service.addKnowledgeCentralToCollectionIfMissing([], knowledgeCentral);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(knowledgeCentral);
      });

      it('should not add a KnowledgeCentral to an array that contains it', () => {
        const knowledgeCentral: IKnowledgeCentral = { id: 'ABC' };
        const knowledgeCentralCollection: IKnowledgeCentral[] = [
          {
            ...knowledgeCentral,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addKnowledgeCentralToCollectionIfMissing(knowledgeCentralCollection, knowledgeCentral);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a KnowledgeCentral to an array that doesn't contain it", () => {
        const knowledgeCentral: IKnowledgeCentral = { id: 'ABC' };
        const knowledgeCentralCollection: IKnowledgeCentral[] = [{ id: 'CBA' }];
        expectedResult = service.addKnowledgeCentralToCollectionIfMissing(knowledgeCentralCollection, knowledgeCentral);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(knowledgeCentral);
      });

      it('should add only unique KnowledgeCentral to an array', () => {
        const knowledgeCentralArray: IKnowledgeCentral[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '97490567-e78d-438d-997b-066a65feb552' }];
        const knowledgeCentralCollection: IKnowledgeCentral[] = [{ id: 'ABC' }];
        expectedResult = service.addKnowledgeCentralToCollectionIfMissing(knowledgeCentralCollection, ...knowledgeCentralArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const knowledgeCentral: IKnowledgeCentral = { id: 'ABC' };
        const knowledgeCentral2: IKnowledgeCentral = { id: 'CBA' };
        expectedResult = service.addKnowledgeCentralToCollectionIfMissing([], knowledgeCentral, knowledgeCentral2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(knowledgeCentral);
        expect(expectedResult).toContain(knowledgeCentral2);
      });

      it('should accept null and undefined values', () => {
        const knowledgeCentral: IKnowledgeCentral = { id: 'ABC' };
        expectedResult = service.addKnowledgeCentralToCollectionIfMissing([], null, knowledgeCentral, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(knowledgeCentral);
      });

      it('should return initial array if no KnowledgeCentral is added', () => {
        const knowledgeCentralCollection: IKnowledgeCentral[] = [{ id: 'ABC' }];
        expectedResult = service.addKnowledgeCentralToCollectionIfMissing(knowledgeCentralCollection, undefined, null);
        expect(expectedResult).toEqual(knowledgeCentralCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
