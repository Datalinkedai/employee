import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IKnowledge } from '../knowledge.model';
import { KnowledgeService } from '../service/knowledge.service';
import { KnowledgeDeleteDialogComponent } from '../delete/knowledge-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-knowledge',
  templateUrl: './knowledge.component.html',
})
export class KnowledgeComponent implements OnInit {
  knowledges?: IKnowledge[];
  isLoading = false;

  constructor(protected knowledgeService: KnowledgeService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.knowledgeService.query().subscribe({
      next: (res: HttpResponse<IKnowledge[]>) => {
        this.isLoading = false;
        this.knowledges = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IKnowledge): string {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(knowledge: IKnowledge): void {
    const modalRef = this.modalService.open(KnowledgeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.knowledge = knowledge;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
