import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IQuestions, getQuestionsIdentifier } from '../questions.model';

export type EntityResponseType = HttpResponse<IQuestions>;
export type EntityArrayResponseType = HttpResponse<IQuestions[]>;

@Injectable({ providedIn: 'root' })
export class QuestionsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/questions');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(questions: IQuestions): Observable<EntityResponseType> {
    return this.http.post<IQuestions>(this.resourceUrl, questions, { observe: 'response' });
  }

  update(questions: IQuestions): Observable<EntityResponseType> {
    return this.http.put<IQuestions>(`${this.resourceUrl}/${getQuestionsIdentifier(questions) as string}`, questions, {
      observe: 'response',
    });
  }

  partialUpdate(questions: IQuestions): Observable<EntityResponseType> {
    return this.http.patch<IQuestions>(`${this.resourceUrl}/${getQuestionsIdentifier(questions) as string}`, questions, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IQuestions>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IQuestions[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addQuestionsToCollectionIfMissing(
    questionsCollection: IQuestions[],
    ...questionsToCheck: (IQuestions | null | undefined)[]
  ): IQuestions[] {
    const questions: IQuestions[] = questionsToCheck.filter(isPresent);
    if (questions.length > 0) {
      const questionsCollectionIdentifiers = questionsCollection.map(questionsItem => getQuestionsIdentifier(questionsItem)!);
      const questionsToAdd = questions.filter(questionsItem => {
        const questionsIdentifier = getQuestionsIdentifier(questionsItem);
        if (questionsIdentifier == null || questionsCollectionIdentifiers.includes(questionsIdentifier)) {
          return false;
        }
        questionsCollectionIdentifiers.push(questionsIdentifier);
        return true;
      });
      return [...questionsToAdd, ...questionsCollection];
    }
    return questionsCollection;
  }
}
