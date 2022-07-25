import { ICandidate } from 'app/entities/candidate/candidate.model';

export interface IKnowledgeCentral {
  id?: string;
  averageResult?: number | null;
  candidateTaken?: ICandidate | null;
}

export class KnowledgeCentral implements IKnowledgeCentral {
  constructor(public id?: string, public averageResult?: number | null, public candidateTaken?: ICandidate | null) {}
}

export function getKnowledgeCentralIdentifier(knowledgeCentral: IKnowledgeCentral): string | undefined {
  return knowledgeCentral.id;
}
