import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IPost, Post } from '../post.model';
import { PostService } from '../service/post.service';
import { ICandidate } from 'app/entities/candidate/candidate.model';
import { CandidateService } from 'app/entities/candidate/service/candidate.service';
import { Status } from 'app/entities/enumerations/status.model';

@Component({
  selector: 'jhi-post-update',
  templateUrl: './post-update.component.html',
})
export class PostUpdateComponent implements OnInit {
  isSaving = false;
  statusValues = Object.keys(Status);

  postedBiesCollection: ICandidate[] = [];

  editForm = this.fb.group({
    id: [],
    postName: [null, [Validators.required, Validators.minLength(5)]],
    description: [],
    minimumExperience: [],
    maximumExperience: [],
    roles: [],
    responsibility: [],
    status: [],
    typeOfEmployment: [],
    postedDate: [],
    postedBy: [],
  });

  constructor(
    protected postService: PostService,
    protected candidateService: CandidateService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ post }) => {
      if (post.id === undefined) {
        const today = dayjs().startOf('day');
        post.postedDate = today;
      }

      this.updateForm(post);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const post = this.createFromForm();
    if (post.id !== undefined) {
      this.subscribeToSaveResponse(this.postService.update(post));
    } else {
      this.subscribeToSaveResponse(this.postService.create(post));
    }
  }

  trackCandidateById(_index: number, item: ICandidate): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPost>>): void {
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

  protected updateForm(post: IPost): void {
    this.editForm.patchValue({
      id: post.id,
      postName: post.postName,
      description: post.description,
      minimumExperience: post.minimumExperience,
      maximumExperience: post.maximumExperience,
      roles: post.roles,
      responsibility: post.responsibility,
      status: post.status,
      typeOfEmployment: post.typeOfEmployment,
      postedDate: post.postedDate ? post.postedDate.format(DATE_TIME_FORMAT) : null,
      postedBy: post.postedBy,
    });

    this.postedBiesCollection = this.candidateService.addCandidateToCollectionIfMissing(this.postedBiesCollection, post.postedBy);
  }

  protected loadRelationshipsOptions(): void {
    this.candidateService
      .query({ filter: 'post-is-null' })
      .pipe(map((res: HttpResponse<ICandidate[]>) => res.body ?? []))
      .pipe(
        map((candidates: ICandidate[]) =>
          this.candidateService.addCandidateToCollectionIfMissing(candidates, this.editForm.get('postedBy')!.value)
        )
      )
      .subscribe((candidates: ICandidate[]) => (this.postedBiesCollection = candidates));
  }

  protected createFromForm(): IPost {
    return {
      ...new Post(),
      id: this.editForm.get(['id'])!.value,
      postName: this.editForm.get(['postName'])!.value,
      description: this.editForm.get(['description'])!.value,
      minimumExperience: this.editForm.get(['minimumExperience'])!.value,
      maximumExperience: this.editForm.get(['maximumExperience'])!.value,
      roles: this.editForm.get(['roles'])!.value,
      responsibility: this.editForm.get(['responsibility'])!.value,
      status: this.editForm.get(['status'])!.value,
      typeOfEmployment: this.editForm.get(['typeOfEmployment'])!.value,
      postedDate: this.editForm.get(['postedDate'])!.value ? dayjs(this.editForm.get(['postedDate'])!.value, DATE_TIME_FORMAT) : undefined,
      postedBy: this.editForm.get(['postedBy'])!.value,
    };
  }
}
