import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IDocuments, Documents } from '../documents.model';
import { DocumentsService } from '../service/documents.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ICandidate } from 'app/entities/candidate/candidate.model';
import { CandidateService } from 'app/entities/candidate/service/candidate.service';
import { DocumentType } from 'app/entities/enumerations/document-type.model';

@Component({
  selector: 'jhi-documents-update',
  templateUrl: './documents-update.component.html',
})
export class DocumentsUpdateComponent implements OnInit {
  isSaving = false;
  documentTypeValues = Object.keys(DocumentType);

  fromCandidatesCollection: ICandidate[] = [];
  verifiedBiesCollection: ICandidate[] = [];

  editForm = this.fb.group({
    id: [],
    documentType: [null, [Validators.required]],
    document: [],
    documentContentType: [],
    documentLink: [],
    documentExpiry: [],
    verified: [],
    fromCandidate: [],
    verifiedBy: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected documentsService: DocumentsService,
    protected candidateService: CandidateService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ documents }) => {
      this.updateForm(documents);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('employeeApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const documents = this.createFromForm();
    if (documents.id !== undefined) {
      this.subscribeToSaveResponse(this.documentsService.update(documents));
    } else {
      this.subscribeToSaveResponse(this.documentsService.create(documents));
    }
  }

  trackCandidateById(_index: number, item: ICandidate): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDocuments>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(documents: IDocuments): void {
    this.editForm.patchValue({
      id: documents.id,
      documentType: documents.documentType,
      document: documents.document,
      documentContentType: documents.documentContentType,
      documentLink: documents.documentLink,
      documentExpiry: documents.documentExpiry,
      verified: documents.verified,
      fromCandidate: documents.fromCandidate,
      verifiedBy: documents.verifiedBy,
    });

    this.fromCandidatesCollection = this.candidateService.addCandidateToCollectionIfMissing(
      this.fromCandidatesCollection,
      documents.fromCandidate
    );
    this.verifiedBiesCollection = this.candidateService.addCandidateToCollectionIfMissing(
      this.verifiedBiesCollection,
      documents.verifiedBy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.candidateService
      .query({ filter: 'documents-is-null' })
      .pipe(map((res: HttpResponse<ICandidate[]>) => res.body ?? []))
      .pipe(
        map((candidates: ICandidate[]) =>
          this.candidateService.addCandidateToCollectionIfMissing(candidates, this.editForm.get('fromCandidate')!.value)
        )
      )
      .subscribe((candidates: ICandidate[]) => (this.fromCandidatesCollection = candidates));

    this.candidateService
      .query({ filter: 'documents-is-null' })
      .pipe(map((res: HttpResponse<ICandidate[]>) => res.body ?? []))
      .pipe(
        map((candidates: ICandidate[]) =>
          this.candidateService.addCandidateToCollectionIfMissing(candidates, this.editForm.get('verifiedBy')!.value)
        )
      )
      .subscribe((candidates: ICandidate[]) => (this.verifiedBiesCollection = candidates));
  }

  protected createFromForm(): IDocuments {
    return {
      ...new Documents(),
      id: this.editForm.get(['id'])!.value,
      documentType: this.editForm.get(['documentType'])!.value,
      documentContentType: this.editForm.get(['documentContentType'])!.value,
      document: this.editForm.get(['document'])!.value,
      documentLink: this.editForm.get(['documentLink'])!.value,
      documentExpiry: this.editForm.get(['documentExpiry'])!.value,
      verified: this.editForm.get(['verified'])!.value,
      fromCandidate: this.editForm.get(['fromCandidate'])!.value,
      verifiedBy: this.editForm.get(['verifiedBy'])!.value,
    };
  }
}
