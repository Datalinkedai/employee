import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOptions, getOptionsIdentifier } from '../options.model';

export type EntityResponseType = HttpResponse<IOptions>;
export type EntityArrayResponseType = HttpResponse<IOptions[]>;

@Injectable({ providedIn: 'root' })
export class OptionsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/options');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(options: IOptions): Observable<EntityResponseType> {
    return this.http.post<IOptions>(this.resourceUrl, options, { observe: 'response' });
  }

  update(options: IOptions): Observable<EntityResponseType> {
    return this.http.put<IOptions>(`${this.resourceUrl}/${getOptionsIdentifier(options) as string}`, options, { observe: 'response' });
  }

  partialUpdate(options: IOptions): Observable<EntityResponseType> {
    return this.http.patch<IOptions>(`${this.resourceUrl}/${getOptionsIdentifier(options) as string}`, options, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IOptions>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOptions[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addOptionsToCollectionIfMissing(optionsCollection: IOptions[], ...optionsToCheck: (IOptions | null | undefined)[]): IOptions[] {
    const options: IOptions[] = optionsToCheck.filter(isPresent);
    if (options.length > 0) {
      const optionsCollectionIdentifiers = optionsCollection.map(optionsItem => getOptionsIdentifier(optionsItem)!);
      const optionsToAdd = options.filter(optionsItem => {
        const optionsIdentifier = getOptionsIdentifier(optionsItem);
        if (optionsIdentifier == null || optionsCollectionIdentifiers.includes(optionsIdentifier)) {
          return false;
        }
        optionsCollectionIdentifiers.push(optionsIdentifier);
        return true;
      });
      return [...optionsToAdd, ...optionsCollection];
    }
    return optionsCollection;
  }
}
