import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IDocuments } from '../documents.model';
import { DocumentsService } from '../service/documents.service';
import { DocumentsDeleteDialogComponent } from '../delete/documents-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-documents',
  templateUrl: './documents.component.html',
})
export class DocumentsComponent implements OnInit {
  documents?: IDocuments[];
  isLoading = false;

  constructor(protected documentsService: DocumentsService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.documentsService.query().subscribe({
      next: (res: HttpResponse<IDocuments[]>) => {
        this.isLoading = false;
        this.documents = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IDocuments): string {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(documents: IDocuments): void {
    const modalRef = this.modalService.open(DocumentsDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.documents = documents;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
