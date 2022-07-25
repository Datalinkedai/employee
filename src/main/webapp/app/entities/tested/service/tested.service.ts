import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITested, getTestedIdentifier } from '../tested.model';

export type EntityResponseType = HttpResponse<ITested>;
export type EntityArrayResponseType = HttpResponse<ITested[]>;

@Injectable({ providedIn: 'root' })
export class TestedService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/testeds');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(tested: ITested): Observable<EntityResponseType> {
    return this.http.post<ITested>(this.resourceUrl, tested, { observe: 'response' });
  }

  update(tested: ITested): Observable<EntityResponseType> {
    return this.http.put<ITested>(`${this.resourceUrl}/${getTestedIdentifier(tested) as string}`, tested, { observe: 'response' });
  }

  partialUpdate(tested: ITested): Observable<EntityResponseType> {
    return this.http.patch<ITested>(`${this.resourceUrl}/${getTestedIdentifier(tested) as string}`, tested, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<ITested>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITested[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTestedToCollectionIfMissing(testedCollection: ITested[], ...testedsToCheck: (ITested | null | undefined)[]): ITested[] {
    const testeds: ITested[] = testedsToCheck.filter(isPresent);
    if (testeds.length > 0) {
      const testedCollectionIdentifiers = testedCollection.map(testedItem => getTestedIdentifier(testedItem)!);
      const testedsToAdd = testeds.filter(testedItem => {
        const testedIdentifier = getTestedIdentifier(testedItem);
        if (testedIdentifier == null || testedCollectionIdentifiers.includes(testedIdentifier)) {
          return false;
        }
        testedCollectionIdentifiers.push(testedIdentifier);
        return true;
      });
      return [...testedsToAdd, ...testedCollection];
    }
    return testedCollection;
  }
}
