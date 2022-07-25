import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT, DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITraining, Training } from '../training.model';

import { TrainingService } from './training.service';

describe('Training Service', () => {
  let service: TrainingService;
  let httpMock: HttpTestingController;
  let elemDefault: ITraining;
  let expectedResult: ITraining | ITraining[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TrainingService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 'AAAAAAA',
      startDate: currentDate,
      startTime: currentDate,
      endTime: currentDate,
      endDate: currentDate,
      type: 'AAAAAAA',
      repeats: false,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          startDate: currentDate.format(DATE_FORMAT),
          startTime: currentDate.format(DATE_TIME_FORMAT),
          endTime: currentDate.format(DATE_TIME_FORMAT),
          endDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Training', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
          startDate: currentDate.format(DATE_FORMAT),
          startTime: currentDate.format(DATE_TIME_FORMAT),
          endTime: currentDate.format(DATE_TIME_FORMAT),
          endDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          startDate: currentDate,
          startTime: currentDate,
          endTime: currentDate,
          endDate: currentDate,
        },
        returnedFromService
      );

      service.create(new Training()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Training', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          startDate: currentDate.format(DATE_FORMAT),
          startTime: currentDate.format(DATE_TIME_FORMAT),
          endTime: currentDate.format(DATE_TIME_FORMAT),
          endDate: currentDate.format(DATE_FORMAT),
          type: 'BBBBBB',
          repeats: true,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          startDate: currentDate,
          startTime: currentDate,
          endTime: currentDate,
          endDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Training', () => {
      const patchObject = Object.assign(
        {
          endTime: currentDate.format(DATE_TIME_FORMAT),
          repeats: true,
        },
        new Training()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          startDate: currentDate,
          startTime: currentDate,
          endTime: currentDate,
          endDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Training', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          startDate: currentDate.format(DATE_FORMAT),
          startTime: currentDate.format(DATE_TIME_FORMAT),
          endTime: currentDate.format(DATE_TIME_FORMAT),
          endDate: currentDate.format(DATE_FORMAT),
          type: 'BBBBBB',
          repeats: true,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          startDate: currentDate,
          startTime: currentDate,
          endTime: currentDate,
          endDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Training', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTrainingToCollectionIfMissing', () => {
      it('should add a Training to an empty array', () => {
        const training: ITraining = { id: 'ABC' };
        expectedResult = service.addTrainingToCollectionIfMissing([], training);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(training);
      });

      it('should not add a Training to an array that contains it', () => {
        const training: ITraining = { id: 'ABC' };
        const trainingCollection: ITraining[] = [
          {
            ...training,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addTrainingToCollectionIfMissing(trainingCollection, training);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Training to an array that doesn't contain it", () => {
        const training: ITraining = { id: 'ABC' };
        const trainingCollection: ITraining[] = [{ id: 'CBA' }];
        expectedResult = service.addTrainingToCollectionIfMissing(trainingCollection, training);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(training);
      });

      it('should add only unique Training to an array', () => {
        const trainingArray: ITraining[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '81706676-ba24-48d9-9da2-9b8e5666e0d9' }];
        const trainingCollection: ITraining[] = [{ id: 'ABC' }];
        expectedResult = service.addTrainingToCollectionIfMissing(trainingCollection, ...trainingArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const training: ITraining = { id: 'ABC' };
        const training2: ITraining = { id: 'CBA' };
        expectedResult = service.addTrainingToCollectionIfMissing([], training, training2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(training);
        expect(expectedResult).toContain(training2);
      });

      it('should accept null and undefined values', () => {
        const training: ITraining = { id: 'ABC' };
        expectedResult = service.addTrainingToCollectionIfMissing([], null, training, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(training);
      });

      it('should return initial array if no Training is added', () => {
        const trainingCollection: ITraining[] = [{ id: 'ABC' }];
        expectedResult = service.addTrainingToCollectionIfMissing(trainingCollection, undefined, null);
        expect(expectedResult).toEqual(trainingCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
