import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IQuestions } from '../questions.model';
import { QuestionsService } from '../service/questions.service';

@Component({
  templateUrl: './questions-delete-dialog.component.html',
})
export class QuestionsDeleteDialogComponent {
  questions?: IQuestions;

  constructor(protected questionsService: QuestionsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.questionsService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
