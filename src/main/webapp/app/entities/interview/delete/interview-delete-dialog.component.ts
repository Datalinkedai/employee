import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IInterview } from '../interview.model';
import { InterviewService } from '../service/interview.service';

@Component({
  templateUrl: './interview-delete-dialog.component.html',
})
export class InterviewDeleteDialogComponent {
  interview?: IInterview;

  constructor(protected interviewService: InterviewService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.interviewService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
