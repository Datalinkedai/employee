import dayjs from 'dayjs/esm';
import { ICandidate } from 'app/entities/candidate/candidate.model';

export interface IInterview {
  id?: string;
  interviewName?: string | null;
  scheduledDate?: dayjs.Dayjs;
  startTime?: dayjs.Dayjs | null;
  endTime?: dayjs.Dayjs | null;
  resceduled?: number | null;
  rescheduleDate?: dayjs.Dayjs | null;
  rescheduleStartTime?: dayjs.Dayjs | null;
  rescheduleEndTIme?: dayjs.Dayjs | null;
  rescheduleApproved?: boolean | null;
  interviewBy?: ICandidate | null;
  rescheduleApprovedBy?: ICandidate | null;
}

export class Interview implements IInterview {
  constructor(
    public id?: string,
    public interviewName?: string | null,
    public scheduledDate?: dayjs.Dayjs,
    public startTime?: dayjs.Dayjs | null,
    public endTime?: dayjs.Dayjs | null,
    public resceduled?: number | null,
    public rescheduleDate?: dayjs.Dayjs | null,
    public rescheduleStartTime?: dayjs.Dayjs | null,
    public rescheduleEndTIme?: dayjs.Dayjs | null,
    public rescheduleApproved?: boolean | null,
    public interviewBy?: ICandidate | null,
    public rescheduleApprovedBy?: ICandidate | null
  ) {
    this.rescheduleApproved = this.rescheduleApproved ?? false;
  }
}

export function getInterviewIdentifier(interview: IInterview): string | undefined {
  return interview.id;
}
