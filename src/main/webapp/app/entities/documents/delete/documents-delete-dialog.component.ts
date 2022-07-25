import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDocuments } from '../documents.model';
import { DocumentsService } from '../service/documents.service';

@Component({
  templateUrl: './documents-delete-dialog.component.html',
})
export class DocumentsDeleteDialogComponent {
  documents?: IDocuments;

  constructor(protected documentsService: DocumentsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.documentsService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
