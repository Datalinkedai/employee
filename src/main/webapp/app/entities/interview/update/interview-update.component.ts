import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IInterview, Interview } from '../interview.model';
import { InterviewService } from '../service/interview.service';
import { ICandidate } from 'app/entities/candidate/candidate.model';
import { CandidateService } from 'app/entities/candidate/service/candidate.service';
import { InterviewStatus } from 'app/entities/enumerations/interview-status.model';

@Component({
  selector: 'jhi-interview-update',
  templateUrl: './interview-update.component.html',
})
export class InterviewUpdateComponent implements OnInit {
  isSaving = false;
  interviewStatusValues = Object.keys(InterviewStatus);

  interviewBiesCollection: ICandidate[] = [];
  rescheduleApprovedBiesCollection: ICandidate[] = [];
  interviewForsCollection: ICandidate[] = [];

  editForm = this.fb.group({
    id: [],
    interviewName: [],
    scheduledDate: [null, [Validators.required]],
    startTime: [],
    endTime: [],
    resceduled: [],
    rescheduleDate: [],
    rescheduleStartTime: [],
    rescheduleEndTime: [],
    rescheduleApproved: [],
    interviewStatus: [],
    interviewBy: [],
    rescheduleApprovedBy: [],
    interviewFor: [],
  });

  constructor(
    protected interviewService: InterviewService,
    protected candidateService: CandidateService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ interview }) => {
      if (interview.id === undefined) {
        const today = dayjs().startOf('day');
        interview.startTime = today;
        interview.endTime = today;
        interview.rescheduleStartTime = today;
        interview.rescheduleEndTime = today;
      }

      this.updateForm(interview);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const interview = this.createFromForm();
    if (interview.id !== undefined) {
      this.subscribeToSaveResponse(this.interviewService.update(interview));
    } else {
      this.subscribeToSaveResponse(this.interviewService.create(interview));
    }
  }

  trackCandidateById(_index: number, item: ICandidate): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInterview>>): void {
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

  protected updateForm(interview: IInterview): void {
    this.editForm.patchValue({
      id: interview.id,
      interviewName: interview.interviewName,
      scheduledDate: interview.scheduledDate,
      startTime: interview.startTime ? interview.startTime.format(DATE_TIME_FORMAT) : null,
      endTime: interview.endTime ? interview.endTime.format(DATE_TIME_FORMAT) : null,
      resceduled: interview.resceduled,
      rescheduleDate: interview.rescheduleDate,
      rescheduleStartTime: interview.rescheduleStartTime ? interview.rescheduleStartTime.format(DATE_TIME_FORMAT) : null,
      rescheduleEndTime: interview.rescheduleEndTime ? interview.rescheduleEndTime.format(DATE_TIME_FORMAT) : null,
      rescheduleApproved: interview.rescheduleApproved,
      interviewStatus: interview.interviewStatus,
      interviewBy: interview.interviewBy,
      rescheduleApprovedBy: interview.rescheduleApprovedBy,
      interviewFor: interview.interviewFor,
    });

    this.interviewBiesCollection = this.candidateService.addCandidateToCollectionIfMissing(
      this.interviewBiesCollection,
      interview.interviewBy
    );
    this.rescheduleApprovedBiesCollection = this.candidateService.addCandidateToCollectionIfMissing(
      this.rescheduleApprovedBiesCollection,
      interview.rescheduleApprovedBy
    );
    this.interviewForsCollection = this.candidateService.addCandidateToCollectionIfMissing(
      this.interviewForsCollection,
      interview.interviewFor
    );
  }

  protected loadRelationshipsOptions(): void {
    this.candidateService
      .query({ filter: 'interview-is-null' })
      .pipe(map((res: HttpResponse<ICandidate[]>) => res.body ?? []))
      .pipe(
        map((candidates: ICandidate[]) =>
          this.candidateService.addCandidateToCollectionIfMissing(candidates, this.editForm.get('interviewBy')!.value)
        )
      )
      .subscribe((candidates: ICandidate[]) => (this.interviewBiesCollection = candidates));

    this.candidateService
      .query({ filter: 'interview-is-null' })
      .pipe(map((res: HttpResponse<ICandidate[]>) => res.body ?? []))
      .pipe(
        map((candidates: ICandidate[]) =>
          this.candidateService.addCandidateToCollectionIfMissing(candidates, this.editForm.get('rescheduleApprovedBy')!.value)
        )
      )
      .subscribe((candidates: ICandidate[]) => (this.rescheduleApprovedBiesCollection = candidates));

    this.candidateService
      .query({ filter: 'interview-is-null' })
      .pipe(map((res: HttpResponse<ICandidate[]>) => res.body ?? []))
      .pipe(
        map((candidates: ICandidate[]) =>
          this.candidateService.addCandidateToCollectionIfMissing(candidates, this.editForm.get('interviewFor')!.value)
        )
      )
      .subscribe((candidates: ICandidate[]) => (this.interviewForsCollection = candidates));
  }

  protected createFromForm(): IInterview {
    return {
      ...new Interview(),
      id: this.editForm.get(['id'])!.value,
      interviewName: this.editForm.get(['interviewName'])!.value,
      scheduledDate: this.editForm.get(['scheduledDate'])!.value,
      startTime: this.editForm.get(['startTime'])!.value ? dayjs(this.editForm.get(['startTime'])!.value, DATE_TIME_FORMAT) : undefined,
      endTime: this.editForm.get(['endTime'])!.value ? dayjs(this.editForm.get(['endTime'])!.value, DATE_TIME_FORMAT) : undefined,
      resceduled: this.editForm.get(['resceduled'])!.value,
      rescheduleDate: this.editForm.get(['rescheduleDate'])!.value,
      rescheduleStartTime: this.editForm.get(['rescheduleStartTime'])!.value
        ? dayjs(this.editForm.get(['rescheduleStartTime'])!.value, DATE_TIME_FORMAT)
        : undefined,
      rescheduleEndTime: this.editForm.get(['rescheduleEndTime'])!.value
        ? dayjs(this.editForm.get(['rescheduleEndTime'])!.value, DATE_TIME_FORMAT)
        : undefined,
      rescheduleApproved: this.editForm.get(['rescheduleApproved'])!.value,
      interviewStatus: this.editForm.get(['interviewStatus'])!.value,
      interviewBy: this.editForm.get(['interviewBy'])!.value,
      rescheduleApprovedBy: this.editForm.get(['rescheduleApprovedBy'])!.value,
      interviewFor: this.editForm.get(['interviewFor'])!.value,
    };
  }
}
