import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IKnowledgeCentral, getKnowledgeCentralIdentifier } from '../knowledge-central.model';

export type EntityResponseType = HttpResponse<IKnowledgeCentral>;
export type EntityArrayResponseType = HttpResponse<IKnowledgeCentral[]>;

@Injectable({ providedIn: 'root' })
export class KnowledgeCentralService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/knowledge-centrals');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(knowledgeCentral: IKnowledgeCentral): Observable<EntityResponseType> {
    return this.http.post<IKnowledgeCentral>(this.resourceUrl, knowledgeCentral, { observe: 'response' });
  }

  update(knowledgeCentral: IKnowledgeCentral): Observable<EntityResponseType> {
    return this.http.put<IKnowledgeCentral>(
      `${this.resourceUrl}/${getKnowledgeCentralIdentifier(knowledgeCentral) as string}`,
      knowledgeCentral,
      { observe: 'response' }
    );
  }

  partialUpdate(knowledgeCentral: IKnowledgeCentral): Observable<EntityResponseType> {
    return this.http.patch<IKnowledgeCentral>(
      `${this.resourceUrl}/${getKnowledgeCentralIdentifier(knowledgeCentral) as string}`,
      knowledgeCentral,
      { observe: 'response' }
    );
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IKnowledgeCentral>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IKnowledgeCentral[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addKnowledgeCentralToCollectionIfMissing(
    knowledgeCentralCollection: IKnowledgeCentral[],
    ...knowledgeCentralsToCheck: (IKnowledgeCentral | null | undefined)[]
  ): IKnowledgeCentral[] {
    const knowledgeCentrals: IKnowledgeCentral[] = knowledgeCentralsToCheck.filter(isPresent);
    if (knowledgeCentrals.length > 0) {
      const knowledgeCentralCollectionIdentifiers = knowledgeCentralCollection.map(
        knowledgeCentralItem => getKnowledgeCentralIdentifier(knowledgeCentralItem)!
      );
      const knowledgeCentralsToAdd = knowledgeCentrals.filter(knowledgeCentralItem => {
        const knowledgeCentralIdentifier = getKnowledgeCentralIdentifier(knowledgeCentralItem);
        if (knowledgeCentralIdentifier == null || knowledgeCentralCollectionIdentifiers.includes(knowledgeCentralIdentifier)) {
          return false;
        }
        knowledgeCentralCollectionIdentifiers.push(knowledgeCentralIdentifier);
        return true;
      });
      return [...knowledgeCentralsToAdd, ...knowledgeCentralCollection];
    }
    return knowledgeCentralCollection;
  }
}
