import { IOptions } from 'app/entities/options/options.model';
import { ITested } from 'app/entities/tested/tested.model';
import { AnswerType } from 'app/entities/enumerations/answer-type.model';

export interface IQuestions {
  id?: string;
  questionName?: string | null;
  answerType?: AnswerType | null;
  imageLinkContentType?: string | null;
  imageLink?: string | null;
  optionLists?: IOptions[] | null;
  tested?: ITested | null;
}

export class Questions implements IQuestions {
  constructor(
    public id?: string,
    public questionName?: string | null,
    public answerType?: AnswerType | null,
    public imageLinkContentType?: string | null,
    public imageLink?: string | null,
    public optionLists?: IOptions[] | null,
    public tested?: ITested | null
  ) {}
}

export function getQuestionsIdentifier(questions: IQuestions): string | undefined {
  return questions.id;
}
