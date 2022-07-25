import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { AnswerType } from 'app/entities/enumerations/answer-type.model';
import { IQuestions, Questions } from '../questions.model';

import { QuestionsService } from './questions.service';

describe('Questions Service', () => {
  let service: QuestionsService;
  let httpMock: HttpTestingController;
  let elemDefault: IQuestions;
  let expectedResult: IQuestions | IQuestions[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(QuestionsService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      questionName: 'AAAAAAA',
      answerType: AnswerType.MCQ,
      imageLinkContentType: 'image/png',
      imageLink: 'AAAAAAA',
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

    it('should create a Questions', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Questions()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Questions', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          questionName: 'BBBBBB',
          answerType: 'BBBBBB',
          imageLink: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Questions', () => {
      const patchObject = Object.assign(
        {
          questionName: 'BBBBBB',
          answerType: 'BBBBBB',
        },
        new Questions()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Questions', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          questionName: 'BBBBBB',
          answerType: 'BBBBBB',
          imageLink: 'BBBBBB',
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

    it('should delete a Questions', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addQuestionsToCollectionIfMissing', () => {
      it('should add a Questions to an empty array', () => {
        const questions: IQuestions = { id: 'ABC' };
        expectedResult = service.addQuestionsToCollectionIfMissing([], questions);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(questions);
      });

      it('should not add a Questions to an array that contains it', () => {
        const questions: IQuestions = { id: 'ABC' };
        const questionsCollection: IQuestions[] = [
          {
            ...questions,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addQuestionsToCollectionIfMissing(questionsCollection, questions);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Questions to an array that doesn't contain it", () => {
        const questions: IQuestions = { id: 'ABC' };
        const questionsCollection: IQuestions[] = [{ id: 'CBA' }];
        expectedResult = service.addQuestionsToCollectionIfMissing(questionsCollection, questions);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(questions);
      });

      it('should add only unique Questions to an array', () => {
        const questionsArray: IQuestions[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '0851421a-b931-40a1-9b80-59dc08a65653' }];
        const questionsCollection: IQuestions[] = [{ id: 'ABC' }];
        expectedResult = service.addQuestionsToCollectionIfMissing(questionsCollection, ...questionsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const questions: IQuestions = { id: 'ABC' };
        const questions2: IQuestions = { id: 'CBA' };
        expectedResult = service.addQuestionsToCollectionIfMissing([], questions, questions2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(questions);
        expect(expectedResult).toContain(questions2);
      });

      it('should accept null and undefined values', () => {
        const questions: IQuestions = { id: 'ABC' };
        expectedResult = service.addQuestionsToCollectionIfMissing([], null, questions, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(questions);
      });

      it('should return initial array if no Questions is added', () => {
        const questionsCollection: IQuestions[] = [{ id: 'ABC' }];
        expectedResult = service.addQuestionsToCollectionIfMissing(questionsCollection, undefined, null);
        expect(expectedResult).toEqual(questionsCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
