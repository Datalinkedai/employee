import { IQuestions } from 'app/entities/questions/questions.model';

export interface IOptions {
  id?: string;
  optionName?: string | null;
  questions?: IQuestions | null;
}

export class Options implements IOptions {
  constructor(public id?: string, public optionName?: string | null, public questions?: IQuestions | null) {}
}

export function getOptionsIdentifier(options: IOptions): string | undefined {
  return options.id;
}
