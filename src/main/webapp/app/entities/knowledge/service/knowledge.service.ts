import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IKnowledge, getKnowledgeIdentifier } from '../knowledge.model';

export type EntityResponseType = HttpResponse<IKnowledge>;
export type EntityArrayResponseType = HttpResponse<IKnowledge[]>;

@Injectable({ providedIn: 'root' })
export class KnowledgeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/knowledges');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(knowledge: IKnowledge): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(knowledge);
    return this.http
      .post<IKnowledge>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(knowledge: IKnowledge): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(knowledge);
    return this.http
      .put<IKnowledge>(`${this.resourceUrl}/${getKnowledgeIdentifier(knowledge) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(knowledge: IKnowledge): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(knowledge);
    return this.http
      .patch<IKnowledge>(`${this.resourceUrl}/${getKnowledgeIdentifier(knowledge) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<IKnowledge>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IKnowledge[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addKnowledgeToCollectionIfMissing(
    knowledgeCollection: IKnowledge[],
    ...knowledgesToCheck: (IKnowledge | null | undefined)[]
  ): IKnowledge[] {
    const knowledges: IKnowledge[] = knowledgesToCheck.filter(isPresent);
    if (knowledges.length > 0) {
      const knowledgeCollectionIdentifiers = knowledgeCollection.map(knowledgeItem => getKnowledgeIdentifier(knowledgeItem)!);
      const knowledgesToAdd = knowledges.filter(knowledgeItem => {
        const knowledgeIdentifier = getKnowledgeIdentifier(knowledgeItem);
        if (knowledgeIdentifier == null || knowledgeCollectionIdentifiers.includes(knowledgeIdentifier)) {
          return false;
        }
        knowledgeCollectionIdentifiers.push(knowledgeIdentifier);
        return true;
      });
      return [...knowledgesToAdd, ...knowledgeCollection];
    }
    return knowledgeCollection;
  }

  protected convertDateFromClient(knowledge: IKnowledge): IKnowledge {
    return Object.assign({}, knowledge, {
      testTaken: knowledge.testTaken?.isValid() ? knowledge.testTaken.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.testTaken = res.body.testTaken ? dayjs(res.body.testTaken) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((knowledge: IKnowledge) => {
        knowledge.testTaken = knowledge.testTaken ? dayjs(knowledge.testTaken) : undefined;
      });
    }
    return res;
  }
}
