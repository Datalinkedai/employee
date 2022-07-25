import dayjs from 'dayjs/esm';
import { ICandidate } from 'app/entities/candidate/candidate.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface IPost {
  id?: string;
  postName?: string;
  description?: string | null;
  minimumExperience?: number | null;
  maximumExperience?: number | null;
  roles?: string | null;
  responsibility?: string | null;
  status?: Status | null;
  typeOfEmployment?: string | null;
  postedDate?: dayjs.Dayjs | null;
  postedBy?: ICandidate | null;
}

export class Post implements IPost {
  constructor(
    public id?: string,
    public postName?: string,
    public description?: string | null,
    public minimumExperience?: number | null,
    public maximumExperience?: number | null,
    public roles?: string | null,
    public responsibility?: string | null,
    public status?: Status | null,
    public typeOfEmployment?: string | null,
    public postedDate?: dayjs.Dayjs | null,
    public postedBy?: ICandidate | null
  ) {}
}

export function getPostIdentifier(post: IPost): string | undefined {
  return post.id;
}
