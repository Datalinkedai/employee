import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IQuestions, Questions } from '../questions.model';
import { QuestionsService } from '../service/questions.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ITested } from 'app/entities/tested/tested.model';
import { TestedService } from 'app/entities/tested/service/tested.service';
import { AnswerType } from 'app/entities/enumerations/answer-type.model';

@Component({
  selector: 'jhi-questions-update',
  templateUrl: './questions-update.component.html',
})
export class QuestionsUpdateComponent implements OnInit {
  isSaving = false;
  answerTypeValues = Object.keys(AnswerType);

  testedsSharedCollection: ITested[] = [];

  editForm = this.fb.group({
    id: [],
    questionName: [],
    answerType: [],
    imageLink: [],
    imageLinkContentType: [],
    tested: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected questionsService: QuestionsService,
    protected testedService: TestedService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ questions }) => {
      this.updateForm(questions);

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
    const questions = this.createFromForm();
    if (questions.id !== undefined) {
      this.subscribeToSaveResponse(this.questionsService.update(questions));
    } else {
      this.subscribeToSaveResponse(this.questionsService.create(questions));
    }
  }

  trackTestedById(_index: number, item: ITested): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IQuestions>>): void {
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

  protected updateForm(questions: IQuestions): void {
    this.editForm.patchValue({
      id: questions.id,
      questionName: questions.questionName,
      answerType: questions.answerType,
      imageLink: questions.imageLink,
      imageLinkContentType: questions.imageLinkContentType,
      tested: questions.tested,
    });

    this.testedsSharedCollection = this.testedService.addTestedToCollectionIfMissing(this.testedsSharedCollection, questions.tested);
  }

  protected loadRelationshipsOptions(): void {
    this.testedService
      .query()
      .pipe(map((res: HttpResponse<ITested[]>) => res.body ?? []))
      .pipe(map((testeds: ITested[]) => this.testedService.addTestedToCollectionIfMissing(testeds, this.editForm.get('tested')!.value)))
      .subscribe((testeds: ITested[]) => (this.testedsSharedCollection = testeds));
  }

  protected createFromForm(): IQuestions {
    return {
      ...new Questions(),
      id: this.editForm.get(['id'])!.value,
      questionName: this.editForm.get(['questionName'])!.value,
      answerType: this.editForm.get(['answerType'])!.value,
      imageLinkContentType: this.editForm.get(['imageLinkContentType'])!.value,
      imageLink: this.editForm.get(['imageLink'])!.value,
      tested: this.editForm.get(['tested'])!.value,
    };
  }
}
