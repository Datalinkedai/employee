import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IKnowledgeCentral } from '../knowledge-central.model';
import { KnowledgeCentralService } from '../service/knowledge-central.service';

@Component({
  templateUrl: './knowledge-central-delete-dialog.component.html',
})
export class KnowledgeCentralDeleteDialogComponent {
  knowledgeCentral?: IKnowledgeCentral;

  constructor(protected knowledgeCentralService: KnowledgeCentralService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.knowledgeCentralService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
