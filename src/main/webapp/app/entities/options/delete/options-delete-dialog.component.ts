import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IOptions } from '../options.model';
import { OptionsService } from '../service/options.service';

@Component({
  templateUrl: './options-delete-dialog.component.html',
})
export class OptionsDeleteDialogComponent {
  options?: IOptions;

  constructor(protected optionsService: OptionsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.optionsService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
