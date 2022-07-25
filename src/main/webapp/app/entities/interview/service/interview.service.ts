import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IInterview, getInterviewIdentifier } from '../interview.model';

export type EntityResponseType = HttpResponse<IInterview>;
export type EntityArrayResponseType = HttpResponse<IInterview[]>;

@Injectable({ providedIn: 'root' })
export class InterviewService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/interviews');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(interview: IInterview): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(interview);
    return this.http
      .post<IInterview>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(interview: IInterview): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(interview);
    return this.http
      .put<IInterview>(`${this.resourceUrl}/${getInterviewIdentifier(interview) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(interview: IInterview): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(interview);
    return this.http
      .patch<IInterview>(`${this.resourceUrl}/${getInterviewIdentifier(interview) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<IInterview>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IInterview[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addInterviewToCollectionIfMissing(
    interviewCollection: IInterview[],
    ...interviewsToCheck: (IInterview | null | undefined)[]
  ): IInterview[] {
    const interviews: IInterview[] = interviewsToCheck.filter(isPresent);
    if (interviews.length > 0) {
      const interviewCollectionIdentifiers = interviewCollection.map(interviewItem => getInterviewIdentifier(interviewItem)!);
      const interviewsToAdd = interviews.filter(interviewItem => {
        const interviewIdentifier = getInterviewIdentifier(interviewItem);
        if (interviewIdentifier == null || interviewCollectionIdentifiers.includes(interviewIdentifier)) {
          return false;
        }
        interviewCollectionIdentifiers.push(interviewIdentifier);
        return true;
      });
      return [...interviewsToAdd, ...interviewCollection];
    }
    return interviewCollection;
  }

  protected convertDateFromClient(interview: IInterview): IInterview {
    return Object.assign({}, interview, {
      scheduledDate: interview.scheduledDate?.isValid() ? interview.scheduledDate.format(DATE_FORMAT) : undefined,
      startTime: interview.startTime?.isValid() ? interview.startTime.toJSON() : undefined,
      endTime: interview.endTime?.isValid() ? interview.endTime.toJSON() : undefined,
      rescheduleDate: interview.rescheduleDate?.isValid() ? interview.rescheduleDate.format(DATE_FORMAT) : undefined,
      rescheduleStartTime: interview.rescheduleStartTime?.isValid() ? interview.rescheduleStartTime.toJSON() : undefined,
      rescheduleEndTIme: interview.rescheduleEndTIme?.isValid() ? interview.rescheduleEndTIme.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.scheduledDate = res.body.scheduledDate ? dayjs(res.body.scheduledDate) : undefined;
      res.body.startTime = res.body.startTime ? dayjs(res.body.startTime) : undefined;
      res.body.endTime = res.body.endTime ? dayjs(res.body.endTime) : undefined;
      res.body.rescheduleDate = res.body.rescheduleDate ? dayjs(res.body.rescheduleDate) : undefined;
      res.body.rescheduleStartTime = res.body.rescheduleStartTime ? dayjs(res.body.rescheduleStartTime) : undefined;
      res.body.rescheduleEndTIme = res.body.rescheduleEndTIme ? dayjs(res.body.rescheduleEndTIme) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((interview: IInterview) => {
        interview.scheduledDate = interview.scheduledDate ? dayjs(interview.scheduledDate) : undefined;
        interview.startTime = interview.startTime ? dayjs(interview.startTime) : undefined;
        interview.endTime = interview.endTime ? dayjs(interview.endTime) : undefined;
        interview.rescheduleDate = interview.rescheduleDate ? dayjs(interview.rescheduleDate) : undefined;
        interview.rescheduleStartTime = interview.rescheduleStartTime ? dayjs(interview.rescheduleStartTime) : undefined;
        interview.rescheduleEndTIme = interview.rescheduleEndTIme ? dayjs(interview.rescheduleEndTIme) : undefined;
      });
    }
    return res;
  }
}
