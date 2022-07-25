import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IQuestions } from '../questions.model';
import { QuestionsService } from '../service/questions.service';
import { QuestionsDeleteDialogComponent } from '../delete/questions-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-questions',
  templateUrl: './questions.component.html',
})
export class QuestionsComponent implements OnInit {
  questions?: IQuestions[];
  isLoading = false;

  constructor(protected questionsService: QuestionsService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.questionsService.query().subscribe({
      next: (res: HttpResponse<IQuestions[]>) => {
        this.isLoading = false;
        this.questions = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IQuestions): string {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(questions: IQuestions): void {
    const modalRef = this.modalService.open(QuestionsDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.questions = questions;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
