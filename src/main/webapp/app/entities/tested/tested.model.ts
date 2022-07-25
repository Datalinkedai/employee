import { IQuestions } from 'app/entities/questions/questions.model';

export interface ITested {
  id?: string;
  testName?: string;
  timeToComplete?: string | null;
  totalQuestions?: number;
  randomize?: boolean | null;
  passingPrcnt?: number | null;
  expiryMonths?: number | null;
  questionLists?: IQuestions[] | null;
}

export class Tested implements ITested {
  constructor(
    public id?: string,
    public testName?: string,
    public timeToComplete?: string | null,
    public totalQuestions?: number,
    public randomize?: boolean | null,
    public passingPrcnt?: number | null,
    public expiryMonths?: number | null,
    public questionLists?: IQuestions[] | null
  ) {
    this.randomize = this.randomize ?? false;
  }
}

export function getTestedIdentifier(tested: ITested): string | undefined {
  return tested.id;
}
