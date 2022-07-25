import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IKnowledge, Knowledge } from '../knowledge.model';
import { KnowledgeService } from '../service/knowledge.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ITested } from 'app/entities/tested/tested.model';
import { TestedService } from 'app/entities/tested/service/tested.service';
import { ICandidate } from 'app/entities/candidate/candidate.model';
import { CandidateService } from 'app/entities/candidate/service/candidate.service';

@Component({
  selector: 'jhi-knowledge-update',
  templateUrl: './knowledge-update.component.html',
})
export class KnowledgeUpdateComponent implements OnInit {
  isSaving = false;

  testsCollection: ITested[] = [];
  candidateTakensCollection: ICandidate[] = [];

  editForm = this.fb.group({
    id: [],
    result: [null, [Validators.min(0), Validators.max(100)]],
    testTaken: [null, [Validators.required]],
    certificate: [],
    certificateContentType: [],
    tests: [],
    candidateTaken: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected knowledgeService: KnowledgeService,
    protected testedService: TestedService,
    protected candidateService: CandidateService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ knowledge }) => {
      if (knowledge.id === undefined) {
        const today = dayjs().startOf('day');
        knowledge.testTaken = today;
      }

      this.updateForm(knowledge);

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
    const knowledge = this.createFromForm();
    if (knowledge.id !== undefined) {
      this.subscribeToSaveResponse(this.knowledgeService.update(knowledge));
    } else {
      this.subscribeToSaveResponse(this.knowledgeService.create(knowledge));
    }
  }

  trackTestedById(_index: number, item: ITested): string {
    return item.id!;
  }

  trackCandidateById(_index: number, item: ICandidate): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IKnowledge>>): void {
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

  protected updateForm(knowledge: IKnowledge): void {
    this.editForm.patchValue({
      id: knowledge.id,
      result: knowledge.result,
      testTaken: knowledge.testTaken ? knowledge.testTaken.format(DATE_TIME_FORMAT) : null,
      certificate: knowledge.certificate,
      certificateContentType: knowledge.certificateContentType,
      tests: knowledge.tests,
      candidateTaken: knowledge.candidateTaken,
    });

    this.testsCollection = this.testedService.addTestedToCollectionIfMissing(this.testsCollection, knowledge.tests);
    this.candidateTakensCollection = this.candidateService.addCandidateToCollectionIfMissing(
      this.candidateTakensCollection,
      knowledge.candidateTaken
    );
  }

  protected loadRelationshipsOptions(): void {
    this.testedService
      .query({ filter: 'knowledge-is-null' })
      .pipe(map((res: HttpResponse<ITested[]>) => res.body ?? []))
      .pipe(map((testeds: ITested[]) => this.testedService.addTestedToCollectionIfMissing(testeds, this.editForm.get('tests')!.value)))
      .subscribe((testeds: ITested[]) => (this.testsCollection = testeds));

    this.candidateService
      .query({ filter: 'knowledge-is-null' })
      .pipe(map((res: HttpResponse<ICandidate[]>) => res.body ?? []))
      .pipe(
        map((candidates: ICandidate[]) =>
          this.candidateService.addCandidateToCollectionIfMissing(candidates, this.editForm.get('candidateTaken')!.value)
        )
      )
      .subscribe((candidates: ICandidate[]) => (this.candidateTakensCollection = candidates));
  }

  protected createFromForm(): IKnowledge {
    return {
      ...new Knowledge(),
      id: this.editForm.get(['id'])!.value,
      result: this.editForm.get(['result'])!.value,
      testTaken: this.editForm.get(['testTaken'])!.value ? dayjs(this.editForm.get(['testTaken'])!.value, DATE_TIME_FORMAT) : undefined,
      certificateContentType: this.editForm.get(['certificateContentType'])!.value,
      certificate: this.editForm.get(['certificate'])!.value,
      tests: this.editForm.get(['tests'])!.value,
      candidateTaken: this.editForm.get(['candidateTaken'])!.value,
    };
  }
}
