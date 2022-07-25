import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IKnowledgeCentral } from '../knowledge-central.model';
import { KnowledgeCentralService } from '../service/knowledge-central.service';
import { KnowledgeCentralDeleteDialogComponent } from '../delete/knowledge-central-delete-dialog.component';

@Component({
  selector: 'jhi-knowledge-central',
  templateUrl: './knowledge-central.component.html',
})
export class KnowledgeCentralComponent implements OnInit {
  knowledgeCentrals?: IKnowledgeCentral[];
  isLoading = false;

  constructor(protected knowledgeCentralService: KnowledgeCentralService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.knowledgeCentralService.query().subscribe({
      next: (res: HttpResponse<IKnowledgeCentral[]>) => {
        this.isLoading = false;
        this.knowledgeCentrals = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IKnowledgeCentral): string {
    return item.id!;
  }

  delete(knowledgeCentral: IKnowledgeCentral): void {
    const modalRef = this.modalService.open(KnowledgeCentralDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.knowledgeCentral = knowledgeCentral;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
