import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICandidate, Candidate } from '../candidate.model';
import { CandidateService } from '../service/candidate.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { Status } from 'app/entities/enumerations/status.model';

@Component({
  selector: 'jhi-candidate-update',
  templateUrl: './candidate-update.component.html',
})
export class CandidateUpdateComponent implements OnInit {
  isSaving = false;
  statusValues = Object.keys(Status);

  usersSharedCollection: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    firstName: [null, [Validators.required, Validators.pattern('^[a-zA-Z]+$')]],
    lastName: [null, [Validators.required, Validators.pattern('^[a-zA-Z]+$')]],
    phoneNumber: [null, [Validators.required, Validators.pattern('^\\d{10}$')]],
    userName: [null, [Validators.required, Validators.pattern('^[a-zA-Z0-9_]{3,40}$')]],
    eductionQualification: [],
    resumeLink: [],
    status: [],
    internalUser: [],
  });

  constructor(
    protected candidateService: CandidateService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ candidate }) => {
      this.updateForm(candidate);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const candidate = this.createFromForm();
    if (candidate.id !== undefined) {
      this.subscribeToSaveResponse(this.candidateService.update(candidate));
    } else {
      this.subscribeToSaveResponse(this.candidateService.create(candidate));
    }
  }

  trackUserById(_index: number, item: IUser): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICandidate>>): void {
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

  protected updateForm(candidate: ICandidate): void {
    this.editForm.patchValue({
      id: candidate.id,
      firstName: candidate.firstName,
      lastName: candidate.lastName,
      phoneNumber: candidate.phoneNumber,
      userName: candidate.userName,
      eductionQualification: candidate.eductionQualification,
      resumeLink: candidate.resumeLink,
      status: candidate.status,
      internalUser: candidate.internalUser,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, candidate.internalUser);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('internalUser')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }

  protected createFromForm(): ICandidate {
    return {
      ...new Candidate(),
      id: this.editForm.get(['id'])!.value,
      firstName: this.editForm.get(['firstName'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      phoneNumber: this.editForm.get(['phoneNumber'])!.value,
      userName: this.editForm.get(['userName'])!.value,
      eductionQualification: this.editForm.get(['eductionQualification'])!.value,
      resumeLink: this.editForm.get(['resumeLink'])!.value,
      status: this.editForm.get(['status'])!.value,
      internalUser: this.editForm.get(['internalUser'])!.value,
    };
  }
}
