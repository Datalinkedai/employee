import dayjs from 'dayjs/esm';
import { ICandidate } from 'app/entities/candidate/candidate.model';
import { DocumentType } from 'app/entities/enumerations/document-type.model';

export interface IDocuments {
  id?: string;
  documentType?: DocumentType;
  documentContentType?: string | null;
  document?: string | null;
  documentLink?: string | null;
  documentExpiry?: dayjs.Dayjs | null;
  verified?: boolean | null;
  fromCandidate?: ICandidate | null;
  verifiedBy?: ICandidate | null;
}

export class Documents implements IDocuments {
  constructor(
    public id?: string,
    public documentType?: DocumentType,
    public documentContentType?: string | null,
    public document?: string | null,
    public documentLink?: string | null,
    public documentExpiry?: dayjs.Dayjs | null,
    public verified?: boolean | null,
    public fromCandidate?: ICandidate | null,
    public verifiedBy?: ICandidate | null
  ) {
    this.verified = this.verified ?? false;
  }
}

export function getDocumentsIdentifier(documents: IDocuments): string | undefined {
  return documents.id;
}
