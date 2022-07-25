import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IKnowledgeCentral, KnowledgeCentral } from '../knowledge-central.model';
import { KnowledgeCentralService } from '../service/knowledge-central.service';
import { ICandidate } from 'app/entities/candidate/candidate.model';
import { CandidateService } from 'app/entities/candidate/service/candidate.service';

@Component({
  selector: 'jhi-knowledge-central-update',
  templateUrl: './knowledge-central-update.component.html',
})
export class KnowledgeCentralUpdateComponent implements OnInit {
  isSaving = false;

  candidateTakensCollection: ICandidate[] = [];

  editForm = this.fb.group({
    id: [],
    averageResult: [null, [Validators.min(0), Validators.max(100)]],
    candidateTaken: [],
  });

  constructor(
    protected knowledgeCentralService: KnowledgeCentralService,
    protected candidateService: CandidateService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ knowledgeCentral }) => {
      this.updateForm(knowledgeCentral);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const knowledgeCentral = this.createFromForm();
    if (knowledgeCentral.id !== undefined) {
      this.subscribeToSaveResponse(this.knowledgeCentralService.update(knowledgeCentral));
    } else {
      this.subscribeToSaveResponse(this.knowledgeCentralService.create(knowledgeCentral));
    }
  }

  trackCandidateById(_index: number, item: ICandidate): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IKnowledgeCentral>>): void {
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

  protected updateForm(knowledgeCentral: IKnowledgeCentral): void {
    this.editForm.patchValue({
      id: knowledgeCentral.id,
      averageResult: knowledgeCentral.averageResult,
      candidateTaken: knowledgeCentral.candidateTaken,
    });

    this.candidateTakensCollection = this.candidateService.addCandidateToCollectionIfMissing(
      this.candidateTakensCollection,
      knowledgeCentral.candidateTaken
    );
  }

  protected loadRelationshipsOptions(): void {
    this.candidateService
      .query({ filter: 'knowledgecentral-is-null' })
      .pipe(map((res: HttpResponse<ICandidate[]>) => res.body ?? []))
      .pipe(
        map((candidates: ICandidate[]) =>
          this.candidateService.addCandidateToCollectionIfMissing(candidates, this.editForm.get('candidateTaken')!.value)
        )
      )
      .subscribe((candidates: ICandidate[]) => (this.candidateTakensCollection = candidates));
  }

  protected createFromForm(): IKnowledgeCentral {
    return {
      ...new KnowledgeCentral(),
      id: this.editForm.get(['id'])!.value,
      averageResult: this.editForm.get(['averageResult'])!.value,
      candidateTaken: this.editForm.get(['candidateTaken'])!.value,
    };
  }
}
