import { IUser } from 'app/entities/user/user.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface ICandidate {
  id?: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  userName?: string;
  eductionQualification?: string | null;
  resumeLink?: string | null;
  status?: Status | null;
  internalUser?: IUser | null;
}

export class Candidate implements ICandidate {
  constructor(
    public id?: string,
    public firstName?: string,
    public lastName?: string,
    public phoneNumber?: string,
    public userName?: string,
    public eductionQualification?: string | null,
    public resumeLink?: string | null,
    public status?: Status | null,
    public internalUser?: IUser | null
  ) {}
}

export function getCandidateIdentifier(candidate: ICandidate): string | undefined {
  return candidate.id;
}
