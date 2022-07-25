import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { DocumentType } from 'app/entities/enumerations/document-type.model';
import { IDocuments, Documents } from '../documents.model';

import { DocumentsService } from './documents.service';

describe('Documents Service', () => {
  let service: DocumentsService;
  let httpMock: HttpTestingController;
  let elemDefault: IDocuments;
  let expectedResult: IDocuments | IDocuments[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DocumentsService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 'AAAAAAA',
      documentType: DocumentType.IMAGE,
      documentContentType: 'image/png',
      document: 'AAAAAAA',
      documentLink: 'AAAAAAA',
      documentExpiry: currentDate,
      verified: false,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          documentExpiry: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Documents', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
          documentExpiry: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          documentExpiry: currentDate,
        },
        returnedFromService
      );

      service.create(new Documents()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Documents', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          documentType: 'BBBBBB',
          document: 'BBBBBB',
          documentLink: 'BBBBBB',
          documentExpiry: currentDate.format(DATE_FORMAT),
          verified: true,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          documentExpiry: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Documents', () => {
      const patchObject = Object.assign(
        {
          documentType: 'BBBBBB',
          document: 'BBBBBB',
          documentLink: 'BBBBBB',
          verified: true,
        },
        new Documents()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          documentExpiry: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Documents', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          documentType: 'BBBBBB',
          document: 'BBBBBB',
          documentLink: 'BBBBBB',
          documentExpiry: currentDate.format(DATE_FORMAT),
          verified: true,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          documentExpiry: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Documents', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addDocumentsToCollectionIfMissing', () => {
      it('should add a Documents to an empty array', () => {
        const documents: IDocuments = { id: 'ABC' };
        expectedResult = service.addDocumentsToCollectionIfMissing([], documents);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(documents);
      });

      it('should not add a Documents to an array that contains it', () => {
        const documents: IDocuments = { id: 'ABC' };
        const documentsCollection: IDocuments[] = [
          {
            ...documents,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addDocumentsToCollectionIfMissing(documentsCollection, documents);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Documents to an array that doesn't contain it", () => {
        const documents: IDocuments = { id: 'ABC' };
        const documentsCollection: IDocuments[] = [{ id: 'CBA' }];
        expectedResult = service.addDocumentsToCollectionIfMissing(documentsCollection, documents);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(documents);
      });

      it('should add only unique Documents to an array', () => {
        const documentsArray: IDocuments[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '14771d72-4a8b-46e4-9443-00907d682317' }];
        const documentsCollection: IDocuments[] = [{ id: 'ABC' }];
        expectedResult = service.addDocumentsToCollectionIfMissing(documentsCollection, ...documentsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const documents: IDocuments = { id: 'ABC' };
        const documents2: IDocuments = { id: 'CBA' };
        expectedResult = service.addDocumentsToCollectionIfMissing([], documents, documents2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(documents);
        expect(expectedResult).toContain(documents2);
      });

      it('should accept null and undefined values', () => {
        const documents: IDocuments = { id: 'ABC' };
        expectedResult = service.addDocumentsToCollectionIfMissing([], null, documents, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(documents);
      });

      it('should return initial array if no Documents is added', () => {
        const documentsCollection: IDocuments[] = [{ id: 'ABC' }];
        expectedResult = service.addDocumentsToCollectionIfMissing(documentsCollection, undefined, null);
        expect(expectedResult).toEqual(documentsCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
