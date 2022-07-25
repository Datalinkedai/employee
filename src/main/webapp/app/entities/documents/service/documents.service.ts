import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDocuments, getDocumentsIdentifier } from '../documents.model';

export type EntityResponseType = HttpResponse<IDocuments>;
export type EntityArrayResponseType = HttpResponse<IDocuments[]>;

@Injectable({ providedIn: 'root' })
export class DocumentsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/documents');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(documents: IDocuments): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(documents);
    return this.http
      .post<IDocuments>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(documents: IDocuments): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(documents);
    return this.http
      .put<IDocuments>(`${this.resourceUrl}/${getDocumentsIdentifier(documents) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(documents: IDocuments): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(documents);
    return this.http
      .patch<IDocuments>(`${this.resourceUrl}/${getDocumentsIdentifier(documents) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<IDocuments>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IDocuments[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addDocumentsToCollectionIfMissing(
    documentsCollection: IDocuments[],
    ...documentsToCheck: (IDocuments | null | undefined)[]
  ): IDocuments[] {
    const documents: IDocuments[] = documentsToCheck.filter(isPresent);
    if (documents.length > 0) {
      const documentsCollectionIdentifiers = documentsCollection.map(documentsItem => getDocumentsIdentifier(documentsItem)!);
      const documentsToAdd = documents.filter(documentsItem => {
        const documentsIdentifier = getDocumentsIdentifier(documentsItem);
        if (documentsIdentifier == null || documentsCollectionIdentifiers.includes(documentsIdentifier)) {
          return false;
        }
        documentsCollectionIdentifiers.push(documentsIdentifier);
        return true;
      });
      return [...documentsToAdd, ...documentsCollection];
    }
    return documentsCollection;
  }

  protected convertDateFromClient(documents: IDocuments): IDocuments {
    return Object.assign({}, documents, {
      documentExpiry: documents.documentExpiry?.isValid() ? documents.documentExpiry.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.documentExpiry = res.body.documentExpiry ? dayjs(res.body.documentExpiry) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((documents: IDocuments) => {
        documents.documentExpiry = documents.documentExpiry ? dayjs(documents.documentExpiry) : undefined;
      });
    }
    return res;
  }
}
