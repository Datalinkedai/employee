import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITested, Tested } from '../tested.model';

import { TestedService } from './tested.service';

describe('Tested Service', () => {
  let service: TestedService;
  let httpMock: HttpTestingController;
  let elemDefault: ITested;
  let expectedResult: ITested | ITested[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TestedService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      testName: 'AAAAAAA',
      timeToComplete: 'PT1S',
      totalQuestions: 0,
      randomize: false,
      passingPrcnt: 0,
      expiryMonths: 0,
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

    it('should create a Tested', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Tested()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Tested', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          testName: 'BBBBBB',
          timeToComplete: 'BBBBBB',
          totalQuestions: 1,
          randomize: true,
          passingPrcnt: 1,
          expiryMonths: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Tested', () => {
      const patchObject = Object.assign(
        {
          testName: 'BBBBBB',
        },
        new Tested()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Tested', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          testName: 'BBBBBB',
          timeToComplete: 'BBBBBB',
          totalQuestions: 1,
          randomize: true,
          passingPrcnt: 1,
          expiryMonths: 1,
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

    it('should delete a Tested', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTestedToCollectionIfMissing', () => {
      it('should add a Tested to an empty array', () => {
        const tested: ITested = { id: 'ABC' };
        expectedResult = service.addTestedToCollectionIfMissing([], tested);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tested);
      });

      it('should not add a Tested to an array that contains it', () => {
        const tested: ITested = { id: 'ABC' };
        const testedCollection: ITested[] = [
          {
            ...tested,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addTestedToCollectionIfMissing(testedCollection, tested);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Tested to an array that doesn't contain it", () => {
        const tested: ITested = { id: 'ABC' };
        const testedCollection: ITested[] = [{ id: 'CBA' }];
        expectedResult = service.addTestedToCollectionIfMissing(testedCollection, tested);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tested);
      });

      it('should add only unique Tested to an array', () => {
        const testedArray: ITested[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '929e57b9-98c6-4390-9216-30f3650f6ab5' }];
        const testedCollection: ITested[] = [{ id: 'ABC' }];
        expectedResult = service.addTestedToCollectionIfMissing(testedCollection, ...testedArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tested: ITested = { id: 'ABC' };
        const tested2: ITested = { id: 'CBA' };
        expectedResult = service.addTestedToCollectionIfMissing([], tested, tested2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tested);
        expect(expectedResult).toContain(tested2);
      });

      it('should accept null and undefined values', () => {
        const tested: ITested = { id: 'ABC' };
        expectedResult = service.addTestedToCollectionIfMissing([], null, tested, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tested);
      });

      it('should return initial array if no Tested is added', () => {
        const testedCollection: ITested[] = [{ id: 'ABC' }];
        expectedResult = service.addTestedToCollectionIfMissing(testedCollection, undefined, null);
        expect(expectedResult).toEqual(testedCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
