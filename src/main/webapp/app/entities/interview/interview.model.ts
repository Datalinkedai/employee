import dayjs from 'dayjs/esm';
import { ICandidate } from 'app/entities/candidate/candidate.model';
import { InterviewStatus } from 'app/entities/enumerations/interview-status.model';

export interface IInterview {
  id?: string;
  interviewName?: string | null;
  scheduledDate?: dayjs.Dayjs;
  startTime?: dayjs.Dayjs | null;
  endTime?: dayjs.Dayjs | null;
  resceduled?: number | null;
  rescheduleDate?: dayjs.Dayjs | null;
  rescheduleStartTime?: dayjs.Dayjs | null;
  rescheduleEndTime?: dayjs.Dayjs | null;
  rescheduleApproved?: boolean | null;
  interviewStatus?: InterviewStatus | null;
  interviewBy?: ICandidate | null;
  rescheduleApprovedBy?: ICandidate | null;
  interviewFor?: ICandidate | null;
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
    public rescheduleEndTime?: dayjs.Dayjs | null,
    public rescheduleApproved?: boolean | null,
    public interviewStatus?: InterviewStatus | null,
    public interviewBy?: ICandidate | null,
    public rescheduleApprovedBy?: ICandidate | null,
    public interviewFor?: ICandidate | null
  ) {
    this.rescheduleApproved = this.rescheduleApproved ?? false;
  }
}

export function getInterviewIdentifier(interview: IInterview): string | undefined {
  return interview.id;
}
