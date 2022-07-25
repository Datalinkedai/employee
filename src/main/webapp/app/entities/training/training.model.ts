import dayjs from 'dayjs/esm';
import { ICandidate } from 'app/entities/candidate/candidate.model';

export interface ITraining {
  id?: string;
  startDate?: dayjs.Dayjs;
  startTime?: dayjs.Dayjs | null;
  endTime?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs;
  type?: string | null;
  repeats?: boolean | null;
  candidateList?: ICandidate | null;
}

export class Training implements ITraining {
  constructor(
    public id?: string,
    public startDate?: dayjs.Dayjs,
    public startTime?: dayjs.Dayjs | null,
    public endTime?: dayjs.Dayjs | null,
    public endDate?: dayjs.Dayjs,
    public type?: string | null,
    public repeats?: boolean | null,
    public candidateList?: ICandidate | null
  ) {
    this.repeats = this.repeats ?? false;
  }
}

export function getTrainingIdentifier(training: ITraining): string | undefined {
  return training.id;
}
