import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT, DATE_TIME_FORMAT } from 'app/config/input.constants';
import { InterviewStatus } from 'app/entities/enumerations/interview-status.model';
import { IInterview, Interview } from '../interview.model';

import { InterviewService } from './interview.service';

describe('Interview Service', () => {
  let service: InterviewService;
  let httpMock: HttpTestingController;
  let elemDefault: IInterview;
  let expectedResult: IInterview | IInterview[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(InterviewService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 'AAAAAAA',
      interviewName: 'AAAAAAA',
      scheduledDate: currentDate,
      startTime: currentDate,
      endTime: currentDate,
      resceduled: 0,
      rescheduleDate: currentDate,
      rescheduleStartTime: currentDate,
      rescheduleEndTime: currentDate,
      rescheduleApproved: false,
      interviewStatus: InterviewStatus.ACCEPTED,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          scheduledDate: currentDate.format(DATE_FORMAT),
          startTime: currentDate.format(DATE_TIME_FORMAT),
          endTime: currentDate.format(DATE_TIME_FORMAT),
          rescheduleDate: currentDate.format(DATE_FORMAT),
          rescheduleStartTime: currentDate.format(DATE_TIME_FORMAT),
          rescheduleEndTime: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Interview', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
          scheduledDate: currentDate.format(DATE_FORMAT),
          startTime: currentDate.format(DATE_TIME_FORMAT),
          endTime: currentDate.format(DATE_TIME_FORMAT),
          rescheduleDate: currentDate.format(DATE_FORMAT),
          rescheduleStartTime: currentDate.format(DATE_TIME_FORMAT),
          rescheduleEndTime: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          scheduledDate: currentDate,
          startTime: currentDate,
          endTime: currentDate,
          rescheduleDate: currentDate,
          rescheduleStartTime: currentDate,
          rescheduleEndTime: currentDate,
        },
        returnedFromService
      );

      service.create(new Interview()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Interview', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          interviewName: 'BBBBBB',
          scheduledDate: currentDate.format(DATE_FORMAT),
          startTime: currentDate.format(DATE_TIME_FORMAT),
          endTime: currentDate.format(DATE_TIME_FORMAT),
          resceduled: 1,
          rescheduleDate: currentDate.format(DATE_FORMAT),
          rescheduleStartTime: currentDate.format(DATE_TIME_FORMAT),
          rescheduleEndTime: currentDate.format(DATE_TIME_FORMAT),
          rescheduleApproved: true,
          interviewStatus: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          scheduledDate: currentDate,
          startTime: currentDate,
          endTime: currentDate,
          rescheduleDate: currentDate,
          rescheduleStartTime: currentDate,
          rescheduleEndTime: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Interview', () => {
      const patchObject = Object.assign(
        {
          scheduledDate: currentDate.format(DATE_FORMAT),
          startTime: currentDate.format(DATE_TIME_FORMAT),
          resceduled: 1,
          rescheduleDate: currentDate.format(DATE_FORMAT),
          rescheduleApproved: true,
        },
        new Interview()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          scheduledDate: currentDate,
          startTime: currentDate,
          endTime: currentDate,
          rescheduleDate: currentDate,
          rescheduleStartTime: currentDate,
          rescheduleEndTime: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Interview', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          interviewName: 'BBBBBB',
          scheduledDate: currentDate.format(DATE_FORMAT),
          startTime: currentDate.format(DATE_TIME_FORMAT),
          endTime: currentDate.format(DATE_TIME_FORMAT),
          resceduled: 1,
          rescheduleDate: currentDate.format(DATE_FORMAT),
          rescheduleStartTime: currentDate.format(DATE_TIME_FORMAT),
          rescheduleEndTime: currentDate.format(DATE_TIME_FORMAT),
          rescheduleApproved: true,
          interviewStatus: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          scheduledDate: currentDate,
          startTime: currentDate,
          endTime: currentDate,
          rescheduleDate: currentDate,
          rescheduleStartTime: currentDate,
          rescheduleEndTime: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Interview', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addInterviewToCollectionIfMissing', () => {
      it('should add a Interview to an empty array', () => {
        const interview: IInterview = { id: 'ABC' };
        expectedResult = service.addInterviewToCollectionIfMissing([], interview);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(interview);
      });

      it('should not add a Interview to an array that contains it', () => {
        const interview: IInterview = { id: 'ABC' };
        const interviewCollection: IInterview[] = [
          {
            ...interview,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addInterviewToCollectionIfMissing(interviewCollection, interview);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Interview to an array that doesn't contain it", () => {
        const interview: IInterview = { id: 'ABC' };
        const interviewCollection: IInterview[] = [{ id: 'CBA' }];
        expectedResult = service.addInterviewToCollectionIfMissing(interviewCollection, interview);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(interview);
      });

      it('should add only unique Interview to an array', () => {
        const interviewArray: IInterview[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '369c5e23-0cd6-4a3b-a5ab-b2479efa035f' }];
        const interviewCollection: IInterview[] = [{ id: 'ABC' }];
        expectedResult = service.addInterviewToCollectionIfMissing(interviewCollection, ...interviewArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const interview: IInterview = { id: 'ABC' };
        const interview2: IInterview = { id: 'CBA' };
        expectedResult = service.addInterviewToCollectionIfMissing([], interview, interview2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(interview);
        expect(expectedResult).toContain(interview2);
      });

      it('should accept null and undefined values', () => {
        const interview: IInterview = { id: 'ABC' };
        expectedResult = service.addInterviewToCollectionIfMissing([], null, interview, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(interview);
      });

      it('should return initial array if no Interview is added', () => {
        const interviewCollection: IInterview[] = [{ id: 'ABC' }];
        expectedResult = service.addInterviewToCollectionIfMissing(interviewCollection, undefined, null);
        expect(expectedResult).toEqual(interviewCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
