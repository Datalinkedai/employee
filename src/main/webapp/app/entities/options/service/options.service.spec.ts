import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IOptions, Options } from '../options.model';

import { OptionsService } from './options.service';

describe('Options Service', () => {
  let service: OptionsService;
  let httpMock: HttpTestingController;
  let elemDefault: IOptions;
  let expectedResult: IOptions | IOptions[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OptionsService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      optionName: 'AAAAAAA',
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

    it('should create a Options', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Options()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Options', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          optionName: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Options', () => {
      const patchObject = Object.assign({}, new Options());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Options', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          optionName: 'BBBBBB',
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

    it('should delete a Options', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addOptionsToCollectionIfMissing', () => {
      it('should add a Options to an empty array', () => {
        const options: IOptions = { id: 'ABC' };
        expectedResult = service.addOptionsToCollectionIfMissing([], options);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(options);
      });

      it('should not add a Options to an array that contains it', () => {
        const options: IOptions = { id: 'ABC' };
        const optionsCollection: IOptions[] = [
          {
            ...options,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addOptionsToCollectionIfMissing(optionsCollection, options);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Options to an array that doesn't contain it", () => {
        const options: IOptions = { id: 'ABC' };
        const optionsCollection: IOptions[] = [{ id: 'CBA' }];
        expectedResult = service.addOptionsToCollectionIfMissing(optionsCollection, options);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(options);
      });

      it('should add only unique Options to an array', () => {
        const optionsArray: IOptions[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '31711533-a6af-4ffb-b36e-49a38f11d730' }];
        const optionsCollection: IOptions[] = [{ id: 'ABC' }];
        expectedResult = service.addOptionsToCollectionIfMissing(optionsCollection, ...optionsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const options: IOptions = { id: 'ABC' };
        const options2: IOptions = { id: 'CBA' };
        expectedResult = service.addOptionsToCollectionIfMissing([], options, options2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(options);
        expect(expectedResult).toContain(options2);
      });

      it('should accept null and undefined values', () => {
        const options: IOptions = { id: 'ABC' };
        expectedResult = service.addOptionsToCollectionIfMissing([], null, options, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(options);
      });

      it('should return initial array if no Options is added', () => {
        const optionsCollection: IOptions[] = [{ id: 'ABC' }];
        expectedResult = service.addOptionsToCollectionIfMissing(optionsCollection, undefined, null);
        expect(expectedResult).toEqual(optionsCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
