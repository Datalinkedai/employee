import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ITraining, Training } from '../training.model';
import { TrainingService } from '../service/training.service';
import { ICandidate } from 'app/entities/candidate/candidate.model';
import { CandidateService } from 'app/entities/candidate/service/candidate.service';

@Component({
  selector: 'jhi-training-update',
  templateUrl: './training-update.component.html',
})
export class TrainingUpdateComponent implements OnInit {
  isSaving = false;

  candidatesSharedCollection: ICandidate[] = [];

  editForm = this.fb.group({
    id: [],
    startDate: [null, [Validators.required]],
    startTime: [],
    endTime: [],
    endDate: [null, [Validators.required]],
    type: [],
    repeats: [],
    candidateList: [],
  });

  constructor(
    protected trainingService: TrainingService,
    protected candidateService: CandidateService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ training }) => {
      if (training.id === undefined) {
        const today = dayjs().startOf('day');
        training.startTime = today;
        training.endTime = today;
      }

      this.updateForm(training);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const training = this.createFromForm();
    if (training.id !== undefined) {
      this.subscribeToSaveResponse(this.trainingService.update(training));
    } else {
      this.subscribeToSaveResponse(this.trainingService.create(training));
    }
  }

  trackCandidateById(_index: number, item: ICandidate): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITraining>>): void {
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

  protected updateForm(training: ITraining): void {
    this.editForm.patchValue({
      id: training.id,
      startDate: training.startDate,
      startTime: training.startTime ? training.startTime.format(DATE_TIME_FORMAT) : null,
      endTime: training.endTime ? training.endTime.format(DATE_TIME_FORMAT) : null,
      endDate: training.endDate,
      type: training.type,
      repeats: training.repeats,
      candidateList: training.candidateList,
    });

    this.candidatesSharedCollection = this.candidateService.addCandidateToCollectionIfMissing(
      this.candidatesSharedCollection,
      training.candidateList
    );
  }

  protected loadRelationshipsOptions(): void {
    this.candidateService
      .query()
      .pipe(map((res: HttpResponse<ICandidate[]>) => res.body ?? []))
      .pipe(
        map((candidates: ICandidate[]) =>
          this.candidateService.addCandidateToCollectionIfMissing(candidates, this.editForm.get('candidateList')!.value)
        )
      )
      .subscribe((candidates: ICandidate[]) => (this.candidatesSharedCollection = candidates));
  }

  protected createFromForm(): ITraining {
    return {
      ...new Training(),
      id: this.editForm.get(['id'])!.value,
      startDate: this.editForm.get(['startDate'])!.value,
      startTime: this.editForm.get(['startTime'])!.value ? dayjs(this.editForm.get(['startTime'])!.value, DATE_TIME_FORMAT) : undefined,
      endTime: this.editForm.get(['endTime'])!.value ? dayjs(this.editForm.get(['endTime'])!.value, DATE_TIME_FORMAT) : undefined,
      endDate: this.editForm.get(['endDate'])!.value,
      type: this.editForm.get(['type'])!.value,
      repeats: this.editForm.get(['repeats'])!.value,
      candidateList: this.editForm.get(['candidateList'])!.value,
    };
  }
}
