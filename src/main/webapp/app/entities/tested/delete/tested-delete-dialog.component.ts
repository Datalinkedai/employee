import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITested } from '../tested.model';
import { TestedService } from '../service/tested.service';

@Component({
  templateUrl: './tested-delete-dialog.component.html',
})
export class TestedDeleteDialogComponent {
  tested?: ITested;

  constructor(protected testedService: TestedService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.testedService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
