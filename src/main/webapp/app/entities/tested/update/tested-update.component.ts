import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ITested, Tested } from '../tested.model';
import { TestedService } from '../service/tested.service';

@Component({
  selector: 'jhi-tested-update',
  templateUrl: './tested-update.component.html',
})
export class TestedUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    testName: [null, [Validators.required]],
    timeToComplete: [],
    totalQuestions: [null, [Validators.required]],
    randomize: [],
    passingPrcnt: [null, [Validators.min(0), Validators.max(100)]],
    expiryMonths: [],
  });

  constructor(protected testedService: TestedService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tested }) => {
      this.updateForm(tested);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tested = this.createFromForm();
    if (tested.id !== undefined) {
      this.subscribeToSaveResponse(this.testedService.update(tested));
    } else {
      this.subscribeToSaveResponse(this.testedService.create(tested));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITested>>): void {
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

  protected updateForm(tested: ITested): void {
    this.editForm.patchValue({
      id: tested.id,
      testName: tested.testName,
      timeToComplete: tested.timeToComplete,
      totalQuestions: tested.totalQuestions,
      randomize: tested.randomize,
      passingPrcnt: tested.passingPrcnt,
      expiryMonths: tested.expiryMonths,
    });
  }

  protected createFromForm(): ITested {
    return {
      ...new Tested(),
      id: this.editForm.get(['id'])!.value,
      testName: this.editForm.get(['testName'])!.value,
      timeToComplete: this.editForm.get(['timeToComplete'])!.value,
      totalQuestions: this.editForm.get(['totalQuestions'])!.value,
      randomize: this.editForm.get(['randomize'])!.value,
      passingPrcnt: this.editForm.get(['passingPrcnt'])!.value,
      expiryMonths: this.editForm.get(['expiryMonths'])!.value,
    };
  }
}
