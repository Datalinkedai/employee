import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IKnowledge } from '../knowledge.model';
import { KnowledgeService } from '../service/knowledge.service';

@Component({
  templateUrl: './knowledge-delete-dialog.component.html',
})
export class KnowledgeDeleteDialogComponent {
  knowledge?: IKnowledge;

  constructor(protected knowledgeService: KnowledgeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.knowledgeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
