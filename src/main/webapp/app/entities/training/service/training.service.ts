import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITraining, getTrainingIdentifier } from '../training.model';

export type EntityResponseType = HttpResponse<ITraining>;
export type EntityArrayResponseType = HttpResponse<ITraining[]>;

@Injectable({ providedIn: 'root' })
export class TrainingService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/trainings');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(training: ITraining): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(training);
    return this.http
      .post<ITraining>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(training: ITraining): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(training);
    return this.http
      .put<ITraining>(`${this.resourceUrl}/${getTrainingIdentifier(training) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(training: ITraining): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(training);
    return this.http
      .patch<ITraining>(`${this.resourceUrl}/${getTrainingIdentifier(training) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<ITraining>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ITraining[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTrainingToCollectionIfMissing(trainingCollection: ITraining[], ...trainingsToCheck: (ITraining | null | undefined)[]): ITraining[] {
    const trainings: ITraining[] = trainingsToCheck.filter(isPresent);
    if (trainings.length > 0) {
      const trainingCollectionIdentifiers = trainingCollection.map(trainingItem => getTrainingIdentifier(trainingItem)!);
      const trainingsToAdd = trainings.filter(trainingItem => {
        const trainingIdentifier = getTrainingIdentifier(trainingItem);
        if (trainingIdentifier == null || trainingCollectionIdentifiers.includes(trainingIdentifier)) {
          return false;
        }
        trainingCollectionIdentifiers.push(trainingIdentifier);
        return true;
      });
      return [...trainingsToAdd, ...trainingCollection];
    }
    return trainingCollection;
  }

  protected convertDateFromClient(training: ITraining): ITraining {
    return Object.assign({}, training, {
      startDate: training.startDate?.isValid() ? training.startDate.format(DATE_FORMAT) : undefined,
      startTime: training.startTime?.isValid() ? training.startTime.toJSON() : undefined,
      endTime: training.endTime?.isValid() ? training.endTime.toJSON() : undefined,
      endDate: training.endDate?.isValid() ? training.endDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.startDate = res.body.startDate ? dayjs(res.body.startDate) : undefined;
      res.body.startTime = res.body.startTime ? dayjs(res.body.startTime) : undefined;
      res.body.endTime = res.body.endTime ? dayjs(res.body.endTime) : undefined;
      res.body.endDate = res.body.endDate ? dayjs(res.body.endDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((training: ITraining) => {
        training.startDate = training.startDate ? dayjs(training.startDate) : undefined;
        training.startTime = training.startTime ? dayjs(training.startTime) : undefined;
        training.endTime = training.endTime ? dayjs(training.endTime) : undefined;
        training.endDate = training.endDate ? dayjs(training.endDate) : undefined;
      });
    }
    return res;
  }
}
