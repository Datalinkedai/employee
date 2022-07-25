import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IOptions, Options } from '../options.model';
import { OptionsService } from '../service/options.service';
import { IQuestions } from 'app/entities/questions/questions.model';
import { QuestionsService } from 'app/entities/questions/service/questions.service';

@Component({
  selector: 'jhi-options-update',
  templateUrl: './options-update.component.html',
})
export class OptionsUpdateComponent implements OnInit {
  isSaving = false;

  questionsSharedCollection: IQuestions[] = [];

  editForm = this.fb.group({
    id: [],
    optionName: [],
    questions: [],
  });

  constructor(
    protected optionsService: OptionsService,
    protected questionsService: QuestionsService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ options }) => {
      this.updateForm(options);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const options = this.createFromForm();
    if (options.id !== undefined) {
      this.subscribeToSaveResponse(this.optionsService.update(options));
    } else {
      this.subscribeToSaveResponse(this.optionsService.create(options));
    }
  }

  trackQuestionsById(_index: number, item: IQuestions): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOptions>>): void {
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

  protected updateForm(options: IOptions): void {
    this.editForm.patchValue({
      id: options.id,
      optionName: options.optionName,
      questions: options.questions,
    });

    this.questionsSharedCollection = this.questionsService.addQuestionsToCollectionIfMissing(
      this.questionsSharedCollection,
      options.questions
    );
  }

  protected loadRelationshipsOptions(): void {
    this.questionsService
      .query()
      .pipe(map((res: HttpResponse<IQuestions[]>) => res.body ?? []))
      .pipe(
        map((questions: IQuestions[]) =>
          this.questionsService.addQuestionsToCollectionIfMissing(questions, this.editForm.get('questions')!.value)
        )
      )
      .subscribe((questions: IQuestions[]) => (this.questionsSharedCollection = questions));
  }

  protected createFromForm(): IOptions {
    return {
      ...new Options(),
      id: this.editForm.get(['id'])!.value,
      optionName: this.editForm.get(['optionName'])!.value,
      questions: this.editForm.get(['questions'])!.value,
    };
  }
}
