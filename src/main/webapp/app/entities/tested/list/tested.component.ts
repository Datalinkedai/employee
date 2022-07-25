import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITested } from '../tested.model';
import { TestedService } from '../service/tested.service';
import { TestedDeleteDialogComponent } from '../delete/tested-delete-dialog.component';

@Component({
  selector: 'jhi-tested',
  templateUrl: './tested.component.html',
})
export class TestedComponent implements OnInit {
  testeds?: ITested[];
  isLoading = false;

  constructor(protected testedService: TestedService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.testedService.query().subscribe({
      next: (res: HttpResponse<ITested[]>) => {
        this.isLoading = false;
        this.testeds = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ITested): string {
    return item.id!;
  }

  delete(tested: ITested): void {
    const modalRef = this.modalService.open(TestedDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.tested = tested;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
