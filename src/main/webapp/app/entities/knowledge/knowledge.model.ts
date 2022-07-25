import dayjs from 'dayjs/esm';
import { ITested } from 'app/entities/tested/tested.model';
import { ICandidate } from 'app/entities/candidate/candidate.model';

export interface IKnowledge {
  id?: string;
  result?: number | null;
  testTaken?: dayjs.Dayjs;
  certificateContentType?: string | null;
  certificate?: string | null;
  tests?: ITested | null;
  candidateTaken?: ICandidate | null;
}

export class Knowledge implements IKnowledge {
  constructor(
    public id?: string,
    public result?: number | null,
    public testTaken?: dayjs.Dayjs,
    public certificateContentType?: string | null,
    public certificate?: string | null,
    public tests?: ITested | null,
    public candidateTaken?: ICandidate | null
  ) {}
}

export function getKnowledgeIdentifier(knowledge: IKnowledge): string | undefined {
  return knowledge.id;
}
