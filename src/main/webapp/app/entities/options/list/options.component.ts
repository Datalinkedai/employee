import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IOptions } from '../options.model';
import { OptionsService } from '../service/options.service';
import { OptionsDeleteDialogComponent } from '../delete/options-delete-dialog.component';

@Component({
  selector: 'jhi-options',
  templateUrl: './options.component.html',
})
export class OptionsComponent implements OnInit {
  options?: IOptions[];
  isLoading = false;

  constructor(protected optionsService: OptionsService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.optionsService.query().subscribe({
      next: (res: HttpResponse<IOptions[]>) => {
        this.isLoading = false;
        this.options = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IOptions): string {
    return item.id!;
  }

  delete(options: IOptions): void {
    const modalRef = this.modalService.open(OptionsDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.options = options;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
